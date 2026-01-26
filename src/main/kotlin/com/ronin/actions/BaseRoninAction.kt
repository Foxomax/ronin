package com.ronin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindowManager
import com.ronin.ui.chat.ChatToolWindow

/**
 * Base class for Ronin actions that work with selected code
 * Refactored to use the new ChatToolWindow API
 */
abstract class BaseRoninAction : AnAction() {

    /**
     * Generates the prompt based on the selected code
     */
    abstract fun getPrompt(code: String): String

    override fun update(e: AnActionEvent) {
        // Enable only if text is selected
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null && editor.selectionModel.hasSelection()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectedText = editor.selectionModel.selectedText ?: return

        val prompt = getPrompt(selectedText)

        // 1. Open the Tool Window if not open
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Ronin Chat")
        toolWindow?.show {
            // 2. Get ChatToolWindow instance and send message
            val chatWindow = ChatToolWindow.getInstance(project)
            if (chatWindow != null) {
                // Use the new API to send the message
                // This will handle everything: adding to UI, calling LLM, processing response, etc.
                chatWindow.sendMessageProgrammatically(prompt)
            }
        }
    }
}
