package com.example.gimmedamoney.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.gimmedamoney.data.model.Message
import com.example.gimmedamoney.data.model.SystemMessage
import com.example.gimmedamoney.data.repository.ChatRepository
class ChatViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chatRepo: ChatRepository = ChatRepository()

    private val groupID: String = checkNotNull(savedStateHandle["groupID"]) {
        "ChatViewModel requires groupID in SavedStateHandle"
    }

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    init {
        chatRepo.listenToMessages(
            groupID = groupID,
            onResult = { newMessages ->
                _messages.clear()
                _messages.addAll(newMessages)
            }
        )

    }

    fun sendTextMessage(senderId: String, text: String) {
        if (text.isBlank()) return
        chatRepo.sendTextMessage(
            groupID = groupID,
            senderId = senderId,
            text = text
        )
    }

    enum class RequestStatus { PENDING, PAID, DECLINED}

    private val _requestStatuses = mutableStateMapOf<String, RequestStatus>()
    val requestStatuses: Map<String, RequestStatus> get() = _requestStatuses

    fun getRequestStatus(requestId: String): RequestStatus = _requestStatuses[requestId] ?: RequestStatus.PENDING

    fun payRequest(requestId: String) {
        _requestStatuses[requestId] = RequestStatus.PAID
        _messages.add(
            SystemMessage(
                text = "You paid request $requestId"
            )
        )
    }

    fun declineRequest(requestId: String) {
        _requestStatuses[requestId] = RequestStatus.DECLINED

        _messages.add(
            SystemMessage(
                text = "You declined request $requestId"
            )
        )
    }
}