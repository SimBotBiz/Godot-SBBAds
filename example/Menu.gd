extends Node

export (String, FILE, "*.tscn") var ConsentTestScene
export (String, FILE, "*.tscn") var AdsTestScene

func _on_ConsentTestBtn_pressed() -> void:
	SceneManager.goto_scene_p(ConsentTestScene)

func _on_AdsTestBtn_pressed() -> void:
	SceneManager.goto_scene_p(AdsTestScene)
