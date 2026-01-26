package com.ronin.ui.chat.components

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory

/**
 * Improved chat input field using EditorTextField for better text handling
 */
class ChatInputField(
    private val project: Project,
    private val onSendMessage: (String) -> Unit
) : EditorTextField(project, PlainTextFileType.INSTANCE) {
    
    init {
        setOneLineMode(false)
        setPlaceholder("Type your message... (Enter to send, Shift+Enter for new line)")
        
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(java.awt.Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        )
        
        // Handle Enter key for sending
        addKeyListener()
        
        // Auto-adjust height based on content
        addDocumentListener()
    }
    
    private fun addKeyListener() {
        addSettingsProvider { editor ->
            editor.contentComponent.addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent) {
                    if (e.keyCode == KeyEvent.VK_ENTER) {
                        if (!e.isShiftDown) {
                            e.consume()
                            sendMessage()
                        }
                    }
                }
            })
        }
    }
    
    private fun addDocumentListener() {
        addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                // Update preferred height based on line count
                val lineCount = document.lineCount.coerceIn(1, 10)
                preferredSize = java.awt.Dimension(
                    preferredSize.width,
                    lineCount * 20 + 10 // Approximate line height
                )
                revalidate()
            }
        })
    }
    
    private fun sendMessage() {
        val message = text.trim()
        if (message.isNotBlank()) {
            onSendMessage(message)
            text = ""
        }
    }
    
    /**
     * Enables or disables the input field
     */
    override fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (enabled) {
            requestFocusInWindow()
        }
    }
}
