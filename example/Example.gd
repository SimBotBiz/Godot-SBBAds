extends Node

var sbbtest = null
var options: Dictionary = {
	"OPTION_1": true,
	"OPTION_2": "true",
	"OPTION_3": 123456,
	"OPTION_4": "Hello Godot!",
}

func _ready() -> void:
	if Engine.has_singleton("SBBTest"):
		sbbtest = Engine.get_singleton("SBBTest")
		sbbtest.init(get_instance_id(), options)

# print log ingame
func print_app(p_text: String) -> void:
	$Log.newline()
	$Log.add_text(p_text)

# this is a callback function from android
func _get_message(p_message: String):
	print(p_message)
	print_app(p_message)