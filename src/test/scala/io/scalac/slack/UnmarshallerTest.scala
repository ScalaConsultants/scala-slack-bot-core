package io.scalac.slack

import io.scalac.slack.api.{ApiTestResponse, AuthTestResponse, RtmStartResponse}
import io.scalac.slack.models.{Away, Channel, ChannelInfo, SlackUser}
import org.joda.time.DateTime
import org.scalatest.{FunSuite, Matchers}
import spray.json._

/**
 * Created on 27.01.15 22:39
 */
class UnmarshallerTest extends FunSuite with Matchers {

  import io.scalac.slack.api.Unmarshallers._

  val url = "https://testapp.slack.com/"
  val team = "testteam"
  val username = "testuser"
  val teamId = "T03DN3GTN"
  val userId = "U03DQKG34"


  test("api.test empty response") {
    val response = /*language=JSON*/ """{"ok":true}"""

    val apiTestResponse = response.parseJson.convertTo[ApiTestResponse]

    apiTestResponse shouldBe 'ok
    apiTestResponse.args should be(None)
    apiTestResponse.error should be(None)

  }

  test("api.test with param") {
    val response = /*language=JSON*/ """{"ok":true,"args":{"name":"mario"}}"""
    val apiTestResponse = response.parseJson.convertTo[ApiTestResponse]
    apiTestResponse shouldBe 'ok
    apiTestResponse.args should be(Some(Map("name" -> "mario")))
    apiTestResponse.error should be(None)

  }
  test("api.test with error") {
    val response = """{"ok":false,"error":"auth_error","args":{"error":"auth_error"}}"""

    val apiTestResponse = response.parseJson.convertTo[ApiTestResponse]

    apiTestResponse should not be 'ok
    apiTestResponse.args should be(Some(Map("error" -> "auth_error")))
    apiTestResponse.error should be(Some("auth_error"))

  }

  test("api.test with error and param") {
    val response = """{"ok":false,"error":"auth_error","args":{"error":"auth_error","name":"mario"}}"""
    val apiTestResponse = response.parseJson.convertTo[ApiTestResponse]

    apiTestResponse should not be 'ok
    apiTestResponse.args should be(Some(Map("error" -> "auth_error", "name" -> "mario")))
    apiTestResponse.error should be(Some("auth_error"))

  }

  test("auth.test successful") {
    val response = s"""{"ok":true,"url":"$url","team":"$team","user":"$username","team_id":"$teamId","user_id":"$userId"}"""
    val authTestResponse = response.parseJson.convertTo[AuthTestResponse]

    authTestResponse shouldBe 'ok

    authTestResponse.error should be(None)
    authTestResponse.url should equal(Some(url))
    authTestResponse.team should equal(Some(team))
    authTestResponse.user should equal(Some(username))
    authTestResponse.user_id should equal(Some(userId))
    authTestResponse.team_id should equal(Some(teamId))
  }

  test("auth.test failed") {
    val response = """{"ok":false,"error":"not_authed"}"""

    val authTestResponse = response.parseJson.convertTo[AuthTestResponse]

    authTestResponse should not be 'ok
    authTestResponse.error should be(Some("not_authed"))
    authTestResponse.url should be(None)
    authTestResponse.team should equal(None)
    authTestResponse.user should equal(None)
    authTestResponse.user_id should equal(None)
    authTestResponse.team_id should equal(None)
  }

  test("rtm.start successful") {
    /* language=JSON */
    val response = """{"channels": [{
                     |    "is_channel": true,
                     |    "name": "general",
                     |    "last_read": "1421772996.000005",
                     |    "creator": "U03DN1GTQ",
                     |    "purpose": {
                     |      "value": "This channel is for team-wide communication and announcements. All team members are in this channel.",
                     |      "creator": "",
                     |      "last_set": 0
                     |    },
                     |    "is_member": true,
                     |    "id": "C03DN1GUJ",
                     |    "unread_count": 1,
                     |    "members": ["U03DKUF05", "U03DKUMKH", "U03DKUTAZ", "U03DL3Q9M", "U03DN1GTQ", "U03DQKG14"],
                     |    "is_general": true,
                     |    "topic": {
                     |      "value": "",
                     |      "creator": "",
                     |      "last_set": 0
                     |    },
                     |    "latest": {
                     |      "subtype": "channel_join",
                     |      "ts": "1421786647.000002",
                     |      "text": "<@U03DQKG14|secretary> has joined the channel",
                     |      "type": "message",
                     |      "user": "U03DQKG14"
                     |    },
                     |    "is_archived": false,
                     |    "created": 1421772055
                     |  }, {
                     |    "is_channel": true,
                     |    "name": "random",
                     |    "creator": "U03DN1GTQ",
                     |    "is_member": false,
                     |    "id": "C03DN1GUN",
                     |    "is_general": false,
                     |    "is_archived": false,
                     |    "created": 1421772055
                     |  }],
                     |  "url": "wss://ms25.slack-msgs.com/websocket/_eQUaO1csLMyoe4p4rUgEIH/W/gEruHxke8x0TNSE0ltMOdO7bHsP_W9mOznr5U1DzWvW7qs6BZulFXKcg0X2giBxV8UaHtptGEK0_F_rUA=",
                     |  "bots": [{
                     |    "id": "B03DL3Q9K",
                     |    "name": "bot",
                     |    "deleted": false,
                     |    "icons": {
                     |      "image_48": "https://slack.global.ssl.fastly.net/26133/plugins/bot/assets/bot_48.png"
                     |    }
                     |  }, {
                     |    "id": "B03DQKG0Y",
                     |    "name": "bot",
                     |    "deleted": false,
                     |    "icons": {
                     |      "image_48": "https://slack.global.ssl.fastly.net/26133/plugins/bot/assets/bot_48.png"
                     |    }
                     |  }],
                     |  "users": [{
                     |    "is_bot": false,
                     |    "name": "benek",
                     |    "tz_offset": 3600,
                     |    "is_admin": false,
                     |    "tz": "Europe/Amsterdam",
                     |    "color": "4bbe2e",
                     |    "is_owner": false,
                     |    "has_files": false,
                     |    "id": "U03DKUF05",
                     |    "presence": "away",
                     |    "profile": {
                     |      "email": "benek@5dots.pl",
                     |      "image_72": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=72&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-72.png",
                     |      "image_48": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=48&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-48.png",
                     |      "image_32": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=32&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-32.png",
                     |      "real_name_normalized": "",
                     |      "real_name": "",
                     |      "image_24": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=24&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-24.png",
                     |      "image_192": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=192&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000.png"
                     |    },
                     |    "tz_label": "Central European Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": false
                     |  }, {
                     |    "is_bot": true,
                     |    "name": "iwan",
                     |    "has_files": false,
                     |    "id": "U03DL3Q9M",
                     |    "presence": "away",
                     |    "profile": {
                     |      "image_original": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_original.jpg",
                     |      "image_72": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_72.jpg",
                     |      "image_48": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_48.jpg",
                     |      "bot_id": "B03DL3Q9K",
                     |      "image_32": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_32.jpg",
                     |      "real_name_normalized": "",
                     |      "real_name": "",
                     |      "image_24": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_24.jpg",
                     |      "image_192": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3462126459_e2907a3b77c466905e17_192.jpg"
                     |    },
                     |    "deleted": true
                     |  }, {
                     |    "is_bot": false,
                     |    "name": "marioosh",
                     |    "tz_offset": 3600,
                     |    "is_admin": true,
                     |    "tz": "Europe/Amsterdam",
                     |    "color": "9f69e7",
                     |    "is_owner": true,
                     |    "has_files": false,
                     |    "id": "U03DN1GTQ",
                     |    "presence": "active",
                     |    "profile": {
                     |      "email": "marioosh@5dots.pl",
                     |      "image_72": "https://secure.gravatar.com/avatar/ab02a07bc137cb73708602cafcd897d4.jpg?s=72&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0020-72.png",
                     |      "image_48": "https://secure.gravatar.com/avatar/ab02a07bc137cb73708602cafcd897d4.jpg?s=48&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0020-48.png",
                     |      "image_32": "https://secure.gravatar.com/avatar/ab02a07bc137cb73708602cafcd897d4.jpg?s=32&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0020-32.png",
                     |      "real_name_normalized": "",
                     |      "real_name": "",
                     |      "image_24": "https://secure.gravatar.com/avatar/ab02a07bc137cb73708602cafcd897d4.jpg?s=24&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0020-24.png",
                     |      "image_192": "https://secure.gravatar.com/avatar/ab02a07bc137cb73708602cafcd897d4.jpg?s=192&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0020.png"
                     |    },
                     |    "tz_label": "Central European Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": true
                     |  }, {
                     |    "is_bot": true,
                     |    "name": "secretary",
                     |    "tz_offset": -28800,
                     |    "is_admin": false,
                     |    "tz": null,
                     |    "color": "e96699",
                     |    "is_owner": false,
                     |    "has_files": false,
                     |    "id": "U03DQKG14",
                     |    "presence": "away",
                     |    "profile": {
                     |      "first_name": "IVAN",
                     |      "image_original": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_original.jpg",
                     |      "image_72": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_48.jpg",
                     |      "image_48": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_48.jpg",
                     |      "bot_id": "B03DQKG0Y",
                     |      "image_32": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_32.jpg",
                     |      "real_name_normalized": "IVAN DEPLOYER",
                     |      "last_name": "DEPLOYER",
                     |      "real_name": "IVAN DEPLOYER",
                     |      "image_24": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_24.jpg",
                     |      "title": "KEEP CHANNEL TIDY",
                     |      "image_192": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2015-01-20/3466670008_0a4adf28d0f251ad032e_48.jpg"
                     |    },
                     |    "tz_label": "Pacific Standard Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "IVAN DEPLOYER",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": false
                     |  }, {
                     |    "is_bot": false,
                     |    "name": "stefek",
                     |    "tz_offset": 3600,
                     |    "is_admin": false,
                     |    "tz": "Europe/Amsterdam",
                     |    "color": "3c989f",
                     |    "is_owner": false,
                     |    "has_files": false,
                     |    "id": "U03DKUTAZ",
                     |    "presence": "away",
                     |    "profile": {
                     |      "email": "stefek@5dots.pl",
                     |      "image_72": "https://secure.gravatar.com/avatar/a4551e4b7d330e59acf4bdda79ac8b21.jpg?s=72&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0002-72.png",
                     |      "image_48": "https://secure.gravatar.com/avatar/a4551e4b7d330e59acf4bdda79ac8b21.jpg?s=48&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F20655%2Fimg%2Favatars%2Fava_0002-48.png",
                     |      "image_32": "https://secure.gravatar.com/avatar/a4551e4b7d330e59acf4bdda79ac8b21.jpg?s=32&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0002-32.png",
                     |      "real_name_normalized": "",
                     |      "real_name": "",
                     |      "image_24": "https://secure.gravatar.com/avatar/a4551e4b7d330e59acf4bdda79ac8b21.jpg?s=24&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0002-24.png",
                     |      "image_192": "https://secure.gravatar.com/avatar/a4551e4b7d330e59acf4bdda79ac8b21.jpg?s=192&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0002.png"
                     |    },
                     |    "tz_label": "Central European Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": false
                     |  }, {
                     |    "is_bot": false,
                     |    "name": "ziuta",
                     |    "tz_offset": 3600,
                     |    "is_admin": false,
                     |    "tz": "Europe/Amsterdam",
                     |    "color": "e7392d",
                     |    "is_owner": false,
                     |    "has_files": false,
                     |    "id": "U03DKUMKH",
                     |    "presence": "away",
                     |    "profile": {
                     |      "email": "ziuta@5dots.pl",
                     |      "image_72": "https://secure.gravatar.com/avatar/92b61c6a2a1efea6208c7faf3ffabea4.jpg?s=72&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0016-72.png",
                     |      "image_48": "https://secure.gravatar.com/avatar/92b61c6a2a1efea6208c7faf3ffabea4.jpg?s=48&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0016-48.png",
                     |      "image_32": "https://secure.gravatar.com/avatar/92b61c6a2a1efea6208c7faf3ffabea4.jpg?s=32&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0016-32.png",
                     |      "real_name_normalized": "",
                     |      "real_name": "",
                     |      "image_24": "https://secure.gravatar.com/avatar/92b61c6a2a1efea6208c7faf3ffabea4.jpg?s=24&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0016-24.png",
                     |      "image_192": "https://secure.gravatar.com/avatar/92b61c6a2a1efea6208c7faf3ffabea4.jpg?s=192&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0016.png"
                     |    },
                     |    "tz_label": "Central European Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": false
                     |  }, {
                     |    "is_bot": false,
                     |    "name": "slackbot",
                     |    "tz_offset": -28800,
                     |    "is_admin": false,
                     |    "tz": null,
                     |    "color": "757575",
                     |    "is_owner": false,
                     |    "id": "USLACKBOT",
                     |    "presence": "active",
                     |    "profile": {
                     |      "first_name": "Slack",
                     |      "email": null,
                     |      "image_72": "https://slack-assets2.s3-us-west-2.amazonaws.com/10068/img/slackbot_72.png",
                     |      "image_48": "https://slack-assets2.s3-us-west-2.amazonaws.com/10068/img/slackbot_48.png",
                     |      "image_32": "https://slack-assets2.s3-us-west-2.amazonaws.com/10068/img/slackbot_32.png",
                     |      "real_name_normalized": "Slack Bot",
                     |      "last_name": "Bot",
                     |      "real_name": "Slack Bot",
                     |      "image_24": "https://slack-assets2.s3-us-west-2.amazonaws.com/10068/img/slackbot_24.png",
                     |      "image_192": "https://slack-assets2.s3-us-west-2.amazonaws.com/10068/img/slackbot_192.png"
                     |    },
                     |    "tz_label": "Pacific Standard Time",
                     |    "is_ultra_restricted": false,
                     |    "status": null,
                     |    "real_name": "Slack Bot",
                     |    "is_restricted": false,
                     |    "deleted": false,
                     |    "is_primary_owner": false
                     |  }],
                     |  "latest_event_ts": "1422397894.000000",
                     |  "self": {
                     |    "name": "secretary",
                     |    "id": "U03DQKG14",
                     |    "manual_presence": "active",
                     |    "prefs": {
                     |      "has_invited": false,
                     |      "no_created_overlays": false,
                     |      "seen_team_menu_tip_card": false,
                     |      "webapp_spellcheck": true,
                     |      "expand_snippets": false,
                     |      "color_names_in_list": true,
                     |      "no_joined_overlays": false,
                     |      "sidebar_behavior": "",
                     |      "email_alerts": "instant",
                     |      "seen_ssb_prompt": false,
                     |      "has_uploaded": false,
                     |      "show_member_presence": true,
                     |      "email_misc": true,
                     |      "time24": false,
                     |      "never_channels": "",
                     |      "push_dm_alert": true,
                     |      "user_colors": "",
                     |      "expand_inline_imgs": true,
                     |      "last_snippet_type": "",
                     |      "emoji_mode": "default",
                     |      "collapsible_by_click": true,
                     |      "load_lato_2": false,
                     |      "mac_speak_speed": 250,
                     |      "ss_emojis": true,
                     |      "no_macssb1_banner": false,
                     |      "highlight_words": "",
                     |      "seen_welcome_2": false,
                     |      "mute_sounds": false,
                     |      "muted_channels": "",
                     |      "seen_member_invite_reminder": false,
                     |      "posts_formatting_guide": true,
                     |      "seen_channels_tip_card": false,
                     |      "sidebar_theme_custom_values": "",
                     |      "at_channel_suppressed_channels": "",
                     |      "f_key_search": false,
                     |      "tz": null,
                     |      "no_text_in_notifications": false,
                     |      "has_created_channel": false,
                     |      "seen_message_input_tip_card": false,
                     |      "arrow_history": false,
                     |      "email_alerts_sleep_until": 0,
                     |      "seen_flexpane_tip_card": false,
                     |      "mac_speak_voice": "com.apple.speech.synthesis.voice.Alex",
                     |      "tab_ui_return_selects": true,
                     |      "privacy_policy_seen": true,
                     |      "mark_msgs_read_immediately": true,
                     |      "push_sound": "b2.mp3",
                     |      "comma_key_prefs": false,
                     |      "collapsible": false,
                     |      "mac_ssb_bullet": true,
                     |      "k_key_omnibox": true,
                     |      "dropbox_enabled": false,
                     |      "growls_enabled": true,
                     |      "welcome_message_hidden": false,
                     |      "all_channels_loud": true,
                     |      "email_weekly": true,
                     |      "seen_search_input_tip_card": false,
                     |      "seen_channel_menu_tip_card": false,
                     |      "search_only_my_channels": false,
                     |      "loud_channels_set": "",
                     |      "search_exclude_channels": "",
                     |      "seen_user_menu_tip_card": false,
                     |      "win_ssb_bullet": true,
                     |      "push_loud_channels_set": "",
                     |      "push_loud_channels": "",
                     |      "enter_is_special_in_tbt": false,
                     |      "full_text_extracts": false,
                     |      "fuzzy_matching": false,
                     |      "push_idle_wait": 2,
                     |      "obey_inline_img_limit": true,
                     |      "seen_domain_invite_reminder": false,
                     |      "sidebar_theme": "default",
                     |      "expand_internal_inline_imgs": true,
                     |      "push_mention_alert": true,
                     |      "new_msg_snd": "knock_brush.mp3",
                     |      "search_exclude_bots": false,
                     |      "convert_emoticons": true,
                     |      "start_scroll_at_oldest": true,
                     |      "fuller_timestamps": false,
                     |      "pagekeys_handled": true,
                     |      "require_at": false,
                     |      "push_mention_channels": "",
                     |      "ls_disabled": false,
                     |      "autoplay_chat_sounds": true,
                     |      "mac_ssb_bounce": "",
                     |      "graphic_emoticons": false,
                     |      "snippet_editor_wrap_long_lines": false,
                     |      "display_real_names_override": 0,
                     |      "push_everything": true,
                     |      "last_seen_at_channel_warning": 0,
                     |      "messages_theme": "default",
                     |      "show_typing": true,
                     |      "speak_growls": false,
                     |      "push_at_channel_suppressed_channels": "",
                     |      "loud_channels": "",
                     |      "search_sort": "timestamp",
                     |      "prompted_for_email_disabling": false,
                     |      "expand_non_media_attachments": true
                     |    },
                     |    "created": 1421786646
                     |  },
                     |  "groups": [],
                     |  "cache_version": "v3-dog",
                     |  "ims": [{
                     |    "last_read": "0000000000.000000",
                     |    "is_open": true,
                     |    "id": "D03DQKG18",
                     |    "unread_count": 0,
                     |    "is_im": true,
                     |    "latest": null,
                     |    "user": "USLACKBOT",
                     |    "created": 1421786647
                     |  }, {
                     |    "last_read": "0000000000.000000",
                     |    "is_open": true,
                     |    "id": "D03DQKG24",
                     |    "unread_count": 0,
                     |    "is_im": true,
                     |    "latest": null,
                     |    "user": "U03DKUF05",
                     |    "created": 1421786647
                     |  }, {
                     |    "last_read": "0000000000.000000",
                     |    "is_open": true,
                     |    "id": "D03DQKG1L",
                     |    "unread_count": 0,
                     |    "is_im": true,
                     |    "latest": null,
                     |    "user": "U03DKUMKH",
                     |    "created": 1421786647
                     |  }, {
                     |    "last_read": "0000000000.000000",
                     |    "is_open": true,
                     |    "id": "D03DQKG1U",
                     |    "unread_count": 0,
                     |    "is_im": true,
                     |    "latest": null,
                     |    "user": "U03DKUTAZ",
                     |    "created": 1421786647
                     |  }, {
                     |    "last_read": "0000000000.000000",
                     |    "is_open": true,
                     |    "id": "D03DQKG1C",
                     |    "unread_count": 0,
                     |    "is_im": true,
                     |    "latest": null,
                     |    "user": "U03DN1GTQ",
                     |    "created": 1421786647
                     |  }],
                     |  "team": {
                     |    "name": "fivedots",
                     |    "domain": "5dots",
                     |    "icon": {
                     |      "image_132": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-132.png",
                     |      "image_68": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-68.png",
                     |      "image_88": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-88.png",
                     |      "image_102": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-102.png",
                     |      "image_44": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-44.png",
                     |      "image_34": "https://slack.global.ssl.fastly.net/28461/img/avatars-teams/ava_0018-34.png",
                     |      "image_default": true
                     |    },
                     |    "over_storage_limit": false,
                     |    "email_domain": "5dots.pl",
                     |    "id": "T03DN1GTN",
                     |    "prefs": {
                     |      "who_can_kick_channels": "admin",
                     |      "warn_before_at_channel": "always",
                     |      "who_can_archive_channels": "regular",
                     |      "dm_retention_type": 0,
                     |      "retention_type": 0,
                     |      "default_channels": ["C03DN1GUJ", "C03DN1GUN"],
                     |      "who_can_post_general": "ra",
                     |      "who_can_at_everyone": "regular",
                     |      "who_can_at_channel": "ra",
                     |      "allow_message_deletion": true,
                     |      "require_at_for_mention": 0,
                     |      "display_real_names": false,
                     |      "who_can_create_channels": "regular",
                     |      "compliance_export_start": 0,
                     |      "who_can_create_groups": "ra",
                     |      "dm_retention_duration": 0,
                     |      "who_can_kick_groups": "regular",
                     |      "retention_duration": 0,
                     |      "msg_edit_window_mins": -1,
                     |      "hide_referers": true,
                     |      "group_retention_duration": 0,
                     |      "group_retention_type": 0
                     |    },
                     |    "msg_edit_window_mins": -1
                     |  },
                     |  "ok": true
                     |}""".stripMargin

    val rtmResponse = response.parseJson.convertTo[RtmStartResponse]

    rtmResponse shouldBe 'ok
    rtmResponse.url should equal("wss://ms25.slack-msgs.com/websocket/_eQUaO1csLMyoe4p4rUgEIH/W/gEruHxke8x0TNSE0ltMOdO7bHsP_W9mOznr5U1DzWvW7qs6BZulFXKcg0X2giBxV8UaHtptGEK0_F_rUA=")
    rtmResponse.users shouldBe 'nonEmpty
    rtmResponse.channels shouldBe 'nonEmpty
    rtmResponse.users.size should equal(7)
    rtmResponse.channels.size should equal(2)
    rtmResponse.self.id should equal("U03DQKG14")
    rtmResponse.self.name should equal("secretary")
  }

  test("long channel unmarshall") {
    /* language=JSON */
    val channelString = """{
                          |    "is_channel": true,
                          |    "name": "general",
                          |    "last_read": "1421772996.000005",
                          |    "creator": "U03DN1GTQ",
                          |    "purpose": {
                          |      "value": "This channel is for team-wide communication and announcements. All team members are in this channel.",
                          |      "creator": "",
                          |      "last_set": 0
                          |    },
                          |    "is_member": true,
                          |    "id": "C03DN1GUJ",
                          |    "unread_count": 1,
                          |    "members": ["U03DKUF05", "U03DKUMKH", "U03DKUTAZ", "U03DL3Q9M", "U03DN1GTQ", "U03DQKG14"],
                          |    "is_general": true,
                          |    "topic": {
                          |      "value": "",
                          |      "creator": "",
                          |      "last_set": 0
                          |    },
                          |    "latest": {
                          |      "subtype": "channel_join",
                          |      "ts": "1421786647.000002",
                          |      "text": "<@U03DQKG14|secretary> has joined the channel",
                          |      "type": "message",
                          |      "user": "U03DQKG14"
                          |    },
                          |    "is_archived": false,
                          |    "created": 1421772055
                          |  }""".stripMargin
    val channel = channelString.parseJson.convertTo[Channel]
    channel shouldBe 'isChannel
    channel shouldBe 'isMember
    channel.name should equal("general")
    channel.creator should equal("U03DN1GTQ")
    channel.id should equal("C03DN1GUJ")
    channel shouldBe 'isGeneral
    channel should not be 'isArchived
    channel.created should equal(new DateTime(1421772055000l))
    channel.purpose should be(Some(ChannelInfo("This channel is for team-wide communication and announcements. All team members are in this channel.", "", 0)))
    channel.topic should be(Some(ChannelInfo("", "", 0)))
    channel.unreadCount should be(Some(1))
    channel.lastRead should be(Some(new DateTime(1421772996000l)))
    channel.members should be(Some(List("U03DKUF05", "U03DKUMKH", "U03DKUTAZ", "U03DL3Q9M", "U03DN1GTQ", "U03DQKG14")))
  }

  test("short channel unmarshall") {
    /* language=JSON */
    val channelString = """{
                          |    "is_channel": true,
                          |    "name": "random",
                          |    "creator": "U03DN1GTQ",
                          |    "is_member": false,
                          |    "id": "C03DN1GUN",
                          |    "is_general": false,
                          |    "is_archived": false,
                          |    "created": 1421772055
                          |  }""".stripMargin


    val channel = channelString.parseJson.convertTo[Channel]
    channel shouldBe 'isChannel
    channel should not be 'isMember
    channel.name should equal("random")
    channel.creator should equal("U03DN1GTQ")
    channel.id should equal("C03DN1GUN")
    channel should not be 'isGeneral
    channel should not be 'isArchived
    channel.created should equal(new DateTime(1421772055000L))
    channel.purpose should be(None)
    channel.topic should be(None)
    channel.unreadCount should be(None)
    channel.lastRead should be(None)
    channel.members should be(None)
  }

  test("Channel topic") {
    val topicString = """{
                        |      "value": "",
                        |      "creator": "",
                        |      "last_set": 0
                        |    }""".stripMargin

    val topic = topicString.parseJson.convertTo[ChannelInfo]
    topic.value should equal("")
    topic.creator should equal("")
    topic.last_set should equal(0)
  }

  test("channel purpose") {

    val purposeString = """{
                          |      "value": "This channel is for team-wide communication and announcements. All team members are in this channel.",
                          |      "creator": "",
                          |      "last_set": 0
                          |    }""".stripMargin
    val purpose = purposeString.parseJson.convertTo[ChannelInfo]

    purpose.value should equal("This channel is for team-wide communication and announcements. All team members are in this channel.")
    purpose.creator should equal("")
    purpose.last_set should equal(0)
  }

  test("user object") {
    /* language=JSON */
    val userString = """{
                       |    "is_bot": false,
                       |    "name": "benek",
                       |    "tz_offset": 3600,
                       |    "is_admin": false,
                       |    "tz": "Europe/Amsterdam",
                       |    "color": "4bbe2e",
                       |    "is_owner": false,
                       |    "has_files": false,
                       |    "id": "U03DKUF05",
                       |    "presence": "away",
                       |    "profile": {
                       |      "email": "benek@5dots.pl",
                       |      "image_72": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=72&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-72.png",
                       |      "image_48": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=48&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-48.png",
                       |      "image_32": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=32&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-32.png",
                       |      "real_name_normalized": "",
                       |      "real_name": "",
                       |      "image_24": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=24&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000-24.png",
                       |      "image_192": "https://secure.gravatar.com/avatar/3d6188e64eb0f7d1156d3bda95452901.jpg?s=192&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F8390%2Fimg%2Favatars%2Fava_0000.png"
                       |    },
                       |    "tz_label": "Central European Time",
                       |    "is_ultra_restricted": false,
                       |    "status": null,
                       |    "real_name": "",
                       |    "is_restricted": false,
                       |    "deleted": false,
                       |    "is_primary_owner": false
                       |  }""".stripMargin

    val user = userString.parseJson.convertTo[SlackUser]

    user.isBot should equal(Some(false))
    user.name should equal("benek")
    user.id should equal("U03DKUF05")
    user should not be 'deleted
    user.isAdmin should equal(Some(false))
    user.isOwner should equal(Some(false))
    user.isPrimaryOwner should equal(Some(false))
    user.isRestricted should equal(Some(false))
    user.isUltraRestricted should equal(Some(false))
    user.hasFiles should equal(Some(false))
    user.presence should equal(Away)
  }
}
