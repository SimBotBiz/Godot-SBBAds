extends Node

var rewarded_ad_uid: String = "ca-app-pub-3940256099942544/5224354917"

var psa: Object = null
var psa_opt: Dictionary = {
	"USE_TEST_ADS": true, 	# this will force test uid,
							# so uid provided in the load stage will be overwritten
}

var rewarded_ad_ready: bool = false
#var interstitial_ad_ready: bool = false


func _ready() -> void:
	if Engine.has_singleton("SBBPlayServicesAds"):
		psa = Engine.get_singleton("SBBPlayServicesAds")
		psa.init(get_instance_id(), psa_opt)


# print log ingame
func print_app(p_text: String) -> void:
	$Log.newline()
	$Log.add_text(p_text)


# Rewarded Ad States
func rewarded_ad_set_state(p_state: String):
	match p_state:
		"loading":
			rewarded_ad_ready = false
			$RewardedAdBtn.disabled = true
			$RewardedAdBtn.text = "Loading..."
		"ready":
			rewarded_ad_ready = true
			$RewardedAdBtn.disabled = false
			$RewardedAdBtn.text = "Show Rewarded Ad"
		"error", _:
			rewarded_ad_ready = false
			$RewardedAdBtn.disabled = false
			$RewardedAdBtn.text = "Load Rewarded Ad"

# Signals
# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

func _on_MenuBtn_pressed() -> void:
	SceneManager.goto_scene_n("Menu")


func _on_RewardedAdBtn_pressed() -> void:
	if psa:
		if rewarded_ad_ready:
			psa.showRewardedAd()
		else:
			psa.loadRewardedAd(rewarded_ad_uid)
			rewarded_ad_set_state("loading")


func _on_InterstitialAdBtn_pressed() -> void:
	pass # Replace with function body.


# Signals from the module
# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

func _get_message(p_message: String) -> void:
	print(p_message)
	print_app(p_message)


# Init
#
func _on_initialization_complete() -> void:
	print("_on_initialization_complete")
	$RewardedAdBtn.disabled = false
	#$InterstitialAdBtn.disabled = false

# Rewarded Ad Load
#
func _on_rewarded_ad_loaded() -> void:
	print("_on_rewarded_ad_loaded")
	rewarded_ad_set_state("ready")


func _on_rewarded_ad_failed_to_loaded(p_error_code: int) -> void:
	print("_on_rewarded_ad_failed_to_loaded, errorCode: " + str(p_error_code))
	rewarded_ad_set_state("error")


# Rewarded Ad Show
#
func _on_rewarded_ad_opened() -> void:
	print("_on_rewarded_ad_opened")


func _on_rewarded_ad_closed() -> void:
	print("_on_rewarded_ad_closed")
	# now is when you want to load the next ad
	psa.loadRewardedAd(rewarded_ad_uid)
	rewarded_ad_set_state("loading")


func _on_user_earned_reward(p_currency: String, p_ammount: int) -> void:
	print("_on_user_earned_reward, currency: " + p_currency + ", ammount: " + str(p_ammount))


func _on_rewarded_ad_failed_to_show(p_error_code: int) -> void:
	print("_on_rewarded_ad_failed_to_show, errorCode: " + str(p_error_code))
	rewarded_ad_set_state("error")