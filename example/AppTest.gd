extends Node

var test: Object = null
var test_opt: Dictionary = {
	"OPTION_1": true,						# Boolean
	"OPTION_2": "true",						# String
	"OPTION_3": "Hello Godot!",				# String
	"OPTION_4": 123,						# Integer
	"OPTION_5": 1.23,						# Double
	"OPTION_6": [1, 2, 3],					# Integer[]
	"OPTION_7": ["one", "two", "three"],	# String[]
}

func _ready() -> void:
	if Engine.has_singleton("SBBTest"):
		test = Engine.get_singleton("SBBTest")
		test.init(get_instance_id(), test_opt)


# print log ingame
func print_app(p_text: String) -> void:
	$Log.newline()
	$Log.add_text(p_text)


func _on_MenuBtn_pressed() -> void:
	SceneManager.goto_scene_n("Menu")


# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
# This are callbacks from the module
# ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

func _get_message(p_message: String) -> void:
	print(p_message)
	print_app(p_message)
