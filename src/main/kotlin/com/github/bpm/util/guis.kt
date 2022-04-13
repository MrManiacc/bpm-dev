package com.github.bpm.util

import com.github.bpmapi.api.graph.connector.CapabilityPin
import com.github.bpmapi.api.graph.connector.VarPin
import com.github.bpmapi.api.graph.node.ISelectable
import com.github.bpmapi.api.type.Type
import imgui.ImGui
import imgui.ImGuiStyle
import imgui.ImVec2
import imgui.ImVec4
import imgui.extension.imnodes.ImNodes
import imgui.flag.*
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.internal.flag.ImGuiDockNodeFlags
import imgui.type.ImInt
import imgui.type.ImString
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.lwjgl.glfw.GLFW


/**
 * This is kind of a wrapper around imgui for ease of use with kotlin.
 */
object Gui {

    /**The constant dockspace id for the main dockspace.**/
    const val DOCKSPACE_ID = "main_dockspace"

    /**Dynamically gets the window long reference.**/
    private val handle: Long get() = Minecraft.getInstance().window.window

    /**We only need to initialize once**/
    private var initialized = false

    /**This stores the glfw backend implementation for imgui**/
    private val imGuiGlfw = ImGuiImplGlfw()

    /**This stores the opengl backend implementation for imgui**/
    private val imGuiGl3 = ImGuiImplGl3()


    /**Used for getting the content region**/
    private val minBuffer = ImVec2()

    /**Used for getting the content region**/
    private val maxBuffer = ImVec2()

    /**
     * This will initialize the gui
     */

    fun init() {
        if (!initialized) {
            initImGui()
            imGuiGlfw.init(handle, true);
            imGuiGl3.init("#version 410"); //Use version of #120 for mac support (max allowed version otherwise exception on mac)
            initialized = true
            println("Created the render context!")
            ImNodes.createContext();
        }
    }

    /**
     * This will initialize the imgui stuff
     */
    private fun initImGui() {
        ImGui.createContext();
        setupStyle(ImGui.getStyle())
        val io = ImGui.getIO();
        io.iniFilename = null
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard)
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable)
//        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
        io.configViewportsNoTaskBarIcon = true
    }

    /**
     * This initializes our style.
     */
    private fun setupStyle(style: ImGuiStyle) {
        style.windowPadding.set(15f, 15f)
        style.framePadding.set(5.0f, 5.0f)
        style.itemSpacing.set(12.0f, 8.0f)
        style.itemInnerSpacing.set(8f, 6f)
        style.windowRounding = 0f
        style.indentSpacing = 25f
        style.scrollbarSize = 15.0f
        style.scrollbarRounding = 9.0f
        style.grabRounding = 3.0f
        setColor(ImGuiCol.Text, ImVec4(0.80f, 0.80f, 0.83f, 1.00f))
        setColor(ImGuiCol.TextDisabled, ImVec4(0.24f, 0.23f, 0.29f, 1.00f))
        setColor(ImGuiCol.WindowBg, ImVec4(0.06f, 0.05f, 0.07f, 1.00f))
        setColor(ImGuiCol.ChildBg, ImVec4(0.07f, 0.07f, 0.09f, 1.00f))
        setColor(ImGuiCol.PopupBg, ImVec4(0.07f, 0.07f, 0.09f, 1.00f))
        setColor(ImGuiCol.Border, ImVec4(0.80f, 0.80f, 0.83f, 0.88f))
        setColor(ImGuiCol.BorderShadow, ImVec4(0.92f, 0.91f, 0.88f, 0.00f))
        setColor(ImGuiCol.FrameBg, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.FrameBgHovered, ImVec4(0.24f, 0.23f, 0.29f, 1.00f))
        setColor(ImGuiCol.FrameBgActive, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.TitleBg, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.TitleBgCollapsed, ImVec4(1.00f, 0.98f, 0.95f, 0.75f))
        setColor(ImGuiCol.TitleBgActive, ImVec4(0.07f, 0.07f, 0.09f, 1.00f))
        setColor(ImGuiCol.MenuBarBg, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.ScrollbarBg, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.ScrollbarGrab, ImVec4(0.80f, 0.80f, 0.83f, 0.31f))
        setColor(ImGuiCol.ScrollbarGrabHovered, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.ScrollbarGrabActive, ImVec4(0.06f, 0.05f, 0.07f, 1.00f))
//        setColor(ImGuiCol.Combo, ImVec4(0.19f, 0.18f, 0.21f, 1.00f))
        setColor(ImGuiCol.CheckMark, ImVec4(0.80f, 0.80f, 0.83f, 0.31f))
        setColor(ImGuiCol.SliderGrab, ImVec4(0.80f, 0.80f, 0.83f, 0.31f))
        setColor(ImGuiCol.SliderGrabActive, ImVec4(0.06f, 0.05f, 0.07f, 1.00f))
        setColor(ImGuiCol.Button, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.ButtonHovered, ImVec4(0.24f, 0.23f, 0.29f, 1.00f))
        setColor(ImGuiCol.ButtonActive, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.Header, ImVec4(0.10f, 0.09f, 0.12f, 1.00f))
        setColor(ImGuiCol.HeaderHovered, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.HeaderActive, ImVec4(0.06f, 0.05f, 0.07f, 1.00f))
        //        setColor(ImGuiCol.Column, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        //        setColor(ImGuiCol.ColumnHovered, ImVec4(0.24f, 0.23f, 0.29f, 1.00f))
        //        setColor(ImGuiCol.ColumnActive, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.ResizeGrip, ImVec4(0.00f, 0.00f, 0.00f, 0.00f))
        setColor(ImGuiCol.ResizeGripHovered, ImVec4(0.56f, 0.56f, 0.58f, 1.00f))
        setColor(ImGuiCol.ResizeGripActive, ImVec4(0.06f, 0.05f, 0.07f, 1.00f))
        //        setColor(ImGuiCol.CloseButton, ImVec4(0.40f, 0.39f, 0.38f, 0.16f))
        //        setColor(ImGuiCol.CloseButtonHovered, ImVec4(0.40f, 0.39f, 0.38f, 0.39f))
        //        setColor(ImGuiCol.CloseButtonActive, ImVec4(0.40f, 0.39f, 0.38f, 1.00f))
        setColor(ImGuiCol.PlotLines, ImVec4(0.40f, 0.39f, 0.38f, 0.63f))
        setColor(ImGuiCol.PlotLinesHovered, ImVec4(0.25f, 1.00f, 0.00f, 1.00f))
        setColor(ImGuiCol.PlotHistogram, ImVec4(0.40f, 0.39f, 0.38f, 0.63f))
        setColor(ImGuiCol.PlotHistogramHovered, ImVec4(0.25f, 1.00f, 0.00f, 1.00f))
        setColor(ImGuiCol.TextSelectedBg, ImVec4(0.25f, 1.00f, 0.00f, 0.43f))
        setColor(ImGuiCol.ModalWindowDimBg, ImVec4(1.00f, 0.98f, 0.95f, 0.73f))

    }

    /**
     * This sets a color for imgui
     */
    private fun setColor(colorIndex: Int, color: ImVec4) {
        val style = ImGui.getStyle()
        style.setColor(colorIndex, color.x, color.y, color.z, color.w)
    }

    /**
     * This will begin the imigui frame
     */
    fun startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    /**
     * This is a magic kotlin wrapper for a frame start with an automatic end.
     */
    inline fun frame(frame: () -> Unit) {
        startFrame()
        frame()
        endFrame()
    }

    /***
     * This will begin and end a node graph
     */
    inline fun nodeGraph(name: String, draw: () -> Unit) {
        ImNodes.beginNodeEditor()
        draw()
        ImNodes.endNodeEditor()
    }

    /**Gets the center position**/
    fun getContentCenter(): ImVec2 {
        ImGui.getWindowContentRegionMin(minBuffer)
        ImGui.getWindowContentRegionMax(maxBuffer)
        val minX = ImGui.getWindowPosX() + minBuffer.x
        val minY = ImGui.getWindowPosY() + minBuffer.y
        val maxX = ImGui.getWindowPosX() + maxBuffer.x
        val maxY = ImGui.getWindowPosY() + maxBuffer.y
        return ImVec2((minX + maxX) / 2f, (minY + maxY) / 20f)
    }

    /**
     * This will end the imgui frame
     */
    fun endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupPtr = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupPtr)
        }
    }

    /**
     * This will create the fullscreen dock-space window.
     */
    inline fun dockspace(name: String, nodes: () -> Unit, editor: () -> Unit) {
        val flags = ImGuiWindowFlags.NoNavFocus.orEquals(
            ImGuiWindowFlags.NoTitleBar,
            ImGuiWindowFlags.NoCollapse,
            ImGuiWindowFlags.NoResize,
            ImGuiWindowFlags.NoMove,
            ImGuiWindowFlags.NoBringToFrontOnFocus
        )
        val size = ImGui.getIO().displaySize
        val window = Minecraft.getInstance().window
        val viewport = ImGui.getMainViewport()
        ImGui.setNextWindowPos(0f, 0f)
        ImGui.setNextWindowSize(size.x, size.y)
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0f, 0f)
        ImGui.begin("Window##$name", flags)
        ImGui.setNextWindowViewport(viewport.id)
        ImGui.popStyleVar()
        var dockspaceID = ImGui.getID(DOCKSPACE_ID)
        val node = imgui.internal.ImGui.dockBuilderGetNode(dockspaceID)
        if (node == null || node.ptr == 0L || node.id == 0) //Null ptr? it we should now create?
            createDock(name)
        dockspaceID = ImGui.getID(DOCKSPACE_ID)
        ImGui.dockSpace(dockspaceID, 0f, 0f, imgui.flag.ImGuiDockNodeFlags.None)
        ImGui.end()
        ImGui.begin("Editor##$name", ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoBringToFrontOnFocus)
        editor()
        ImGui.end()
        ImGui.begin("Nodes##$name", ImGuiWindowFlags.NoCollapse or ImGuiWindowFlags.NoBringToFrontOnFocus)
        nodes()
        ImGui.end()
    }

    /**
     * This internally creates the dock when it's not present.
     */
    fun createDock(name: String) {
        val viewport = ImGui.getWindowViewport()
        val dockspaceID = ImGui.getID(DOCKSPACE_ID)
        imgui.internal.ImGui.dockBuilderRemoveNode(dockspaceID)
        imgui.internal.ImGui.dockBuilderAddNode(dockspaceID, ImGuiDockNodeFlags.DockSpace)
        imgui.internal.ImGui.dockBuilderSetNodeSize(dockspaceID, viewport.sizeX, viewport.sizeY)
        val dockMainId = ImInt(dockspaceID)
        val dockLeft: Int =
            imgui.internal.ImGui.dockBuilderSplitNode(dockMainId.get(), ImGuiDir.Right, 0.25f, null, dockMainId)
        imgui.internal.ImGui.dockBuilderDockWindow("Editor##$name", dockMainId.get())
        imgui.internal.ImGui.dockBuilderDockWindow("Nodes##$name", dockLeft)
        imgui.internal.ImGui.dockBuilderFinish(dockspaceID)
    }

}

private val intArrayBuffer = IntArray(1)
private val floatArrayBuffer = FloatArray(1)
private val stringBuffer = ImString()
private val blockPosBuffer = IntArray(3)
private val currentFace = ImInt()
private val faces = Direction.values().map { it.name }.toTypedArray()

internal fun CapabilityPin.drawValue() {
    if (ImGui.beginListBox("inventory")) {
        for (i in 0 until slots) {
            val item = getStackInSlot(i)
            if (!item.isEmpty) {
                ImGui.text(item.toString())
            }
        }
        ImGui.endListBox()
    }

    ImGui.text("Energy: ${this.energyStored}")
}

internal fun VarPin.drawValue(overrideName: String? = null, padding: Float = 38f, drawSelectable: Boolean = true) {
    val name = (overrideName ?: this.name).replace(" in", "").replace(" out", "")
    var updated = false
    when (type) {
        Type.INT -> {
            intArrayBuffer[0] = if (value is Int) value as Int else 0
            if (ImGui.dragInt("$name##$id", intArrayBuffer)) {
                this.value = intArrayBuffer[0]
                info { "Updated value to $value" }
                updated = true
            }

        }
        Type.FLOAT -> {
            floatArrayBuffer[0] = if (value is Float) value as Float else 0f
            if (ImGui.dragFloat("$name##$id", floatArrayBuffer)) {
                this.value = floatArrayBuffer[0]
                info { "Updated value to $value" }
                updated = true
            }
        }
        Type.BOOLEAN -> {
            val value = if (value is Boolean) value as Boolean else false
            if (ImGui.button("$value##$id")) {
                this.value = !value
                info { "Updated value to $value" }
                updated = true
            }
            ImGui.sameLine()
            ImGui.dummy(if (value) padding else padding - 7, 0f)
            ImGui.sameLine()
            ImGui.text(name)

        }
        Type.STRING -> {
            stringBuffer.set(if (value is String) value else "")
            if (ImGui.inputText("$name##$id", stringBuffer)) {
                this.value = stringBuffer.get()
                info { "Updated value to $value" }
                updated = true
            }
        }
        Type.BLOCK_POS -> {
            if (value !is BlockPos) value = BlockPos.ZERO
            val pos = value as BlockPos

            blockPosBuffer[0] = pos.x
            blockPosBuffer[1] = pos.y
            blockPosBuffer[2] = pos.z
            if (ImGui.inputInt3("$name##$id", blockPosBuffer)) {
                this.value = BlockPos(blockPosBuffer[0], blockPosBuffer[1], blockPosBuffer[2])
                updated = true
            }
            if (parent is ISelectable && drawSelectable) {
                if (ImGui.button("select##${id}")) {
                    Selections.start(parent as ISelectable)
                }
            }
        }
        Type.BLOCK_FACE -> {
            if (value !is Direction) value = Direction.NORTH
            currentFace.set((value as Direction).ordinal)
            if (ImGui.combo("$name##$id", currentFace, faces)) {
                this.value = Direction.values()[currentFace.get()]
                updated = true
            }
            if (parent is ISelectable && drawSelectable) {
                if (ImGui.button("select##${id}")) {
                    Selections.start(parent as ISelectable)
                }
            }
        }
    }

    if (updated) this.links.values.mapNotNull {
        this.parent.graph.findByInputId(it) ?: this.parent.graph.findByOutputId(it)
    }.filterIsInstance<VarPin>().forEach {
        it.value = this.value
    }
}