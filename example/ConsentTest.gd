extends Node

var publisher_ids: Array = ["pub-0123456789012345"]
var privacy_url: String = "https://gdpr.eu/privacy-notice/"

var consent: Object = null
var consent_opt: Dictionary = {
	"DEBUG_GEOGRAPHY": "DEBUG_GEOGRAPHY_EEA",
	"TAG_FOR_UNDER_AGE_OF_CONSENT": false,
}

var collect_consent_opt: Dictionary = {
	"PERSONALIZED_ADS": true,
	"NON_PERSONALIZED_ADS": true,
	"AD_FREE": false,
}


func _ready() -> void:

	# get and init SBBConsent
	if Engine.has_singleton("SBBConsent"):
		consent = Engine.get_singleton("SBBConsent")
		consent.init(get_instance_id(), consent_opt)
		consent.requestConsentInfoUpdate(publisher_ids)


# print log in-game
func print_app(p_text: String) -> void:
	$Log.newline()
	$Log.add_text(p_text)


# Signals
# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

func _on_MenuBtn_pressed() -> void:
	SceneManager.goto_scene_n("Menu")


func _on_CollectConsentBtn_pressed() -> void:
	consent.collectConsent(privacy_url, collect_consent_opt)


# set global non personalized ads flag
func set_npa(p_consent_status) -> void:
	match p_consent_status:
		"PERSONALIZED":
			Globals.non_personalized_ads = false
			$CollectConsentBtn.text = "Update Consent"
		"NON_PERSONALIZED":
			Globals.non_personalized_ads = true
			$CollectConsentBtn.text = "Update Consent"
		"UNKNOWN":
			# we need to collect consent
			$CollectConsentBtn.text = "Collect Consent"


# Signals from the module
# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

func _get_message(p_message: String) -> void:
	print(p_message)
	print_app(p_message)


# Request Consent Info Update
#
func _on_consent_info_updated(p_consent_status: String, p_isrlin_eea_or_unknown: bool) -> void:
	print("_on_consent_info_updated, consentStatus: " + p_consent_status + ", isRequestLocationInEeaOrUnknown: " + str(p_isrlin_eea_or_unknown))

	# if in EEA or Consent is UNKNOWN
	if (p_isrlin_eea_or_unknown):
		set_npa(p_consent_status)
		$CollectConsentBtn.disabled = false


func _on_failed_to_update_consent_info(p_error_description: String) -> void:
	print("_on_failed_to_update_consent_info, errorDescription: " + p_error_description)


# Collect Consent
#
func _on_consent_form_loaded() -> void:
	print("_on_consent_form_loaded")


func _on_consent_form_opened() -> void:
	print("_on_consent_form_opened")


func _on_consent_form_closed(p_consent_status: String, p_user_prefer_adfree: bool) -> void:
	print("_on_consent_form_closed, consentStatus: " + p_consent_status + ", userPrefersAdFree: " + str(p_user_prefer_adfree))

	# update consent
	set_npa(p_consent_status)

	# p_user_prefer_adfree
	# did you offer an "ad free" (aka paid) version of your app?
	# now is when you do the magic to get paid


func _on_consent_form_error(p_error_description: String) -> void:
	print("_on_consent_form_error, errorDescription: " + p_error_description)
