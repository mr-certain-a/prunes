package monster.nor.prunes.toolbox.opt

import javafx.application.Application
import javafx.application.Application.launch
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import java.nio.file.Path
import java.util.*

class DropWindow(private val title: String = "Unknown", private val dropped: (DropWindow, Path)->Unit = { _, _->}) {

    private val uuid = UUID.randomUUID().toString()
    private var app: DropWindowApplication? = null

    companion object {
        private val map = mutableMapOf<String, DropWindow>()
        private fun search(id: String): DropWindow? {
            return map[id]
        }
    }

    private fun setApp(application: DropWindowApplication) {
        app = application
    }

    fun drawText(s: Any) {
        app?.drawText(s)
    }

    class DropWindowApplication: Application() {
        private lateinit var vbox: VBox
        private var window: DropWindow? = null

        fun drawText(s: Any) {
            Platform.runLater {
                vbox.children.add(Text(s.toString()))
            }
        }


        override fun init() {
            super.init()
            parameters.raw.firstOrNull()?.let {
                window = search(it)
                window?.setApp(this)
            }
        }

        override fun start(primaryStage: Stage) {
            VBox().apply {
                vbox = this
                setOnDragOver {
                    if (it.dragboard.hasFiles()) {
                        it.acceptTransferModes(TransferMode.MOVE)
                    }
                }
                setOnDragDropped { event ->
                    val board = event.dragboard
                    when (board.hasFiles()) {
                        true -> {
                            board.files.forEach { file ->
                                window?.let {
                                    it.dropped(it, file.toPath())
                                }
                            }
                            event.isDropCompleted = true
                        }
                        else -> event.isDropCompleted = false
                    }
                }
            }.let {
                primaryStage.scene = Scene(it, 400.0, 200.0)
            }

            primaryStage.title = window?.title ?: "Unknown"
            primaryStage.isAlwaysOnTop = true
            primaryStage.show()
        }
    }

    init {
        map[uuid] = this
        launch(DropWindowApplication().javaClass, uuid)
    }
}
