package com.cornellappdev.resell.android.model.api

import android.util.Log
import com.cornellappdev.resell.android.model.chats.BuyerSellerData
import com.cornellappdev.resell.android.model.classes.ResellApiResponse
import com.cornellappdev.resell.android.model.core.UserInfoRepository
import com.cornellappdev.resell.android.model.login.FireStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val fireStoreRepository: FireStoreRepository,
    private val userInfoRepository: UserInfoRepository
) {

    private val _buyersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<BuyerSellerData>>>(ResellApiResponse.Pending)
    val buyersHistoryFlow = _buyersHistoryFlow.asStateFlow()

    private val _sellersHistoryFlow =
        MutableStateFlow<ResellApiResponse<List<BuyerSellerData>>>(ResellApiResponse.Pending)
    val sellersHistoryFlow = _sellersHistoryFlow.asStateFlow()

    /**
     * Starts loading the chat history and sends it down [sellersHistoryFlow].
     */
    fun fetchSellersHistory() {
        _sellersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sellerData =
                    fireStoreRepository.getSellerHistory(userInfoRepository.getEmail()!!)
                _sellersHistoryFlow.value = ResellApiResponse.Success(sellerData)
            } catch (e: Exception) {
                _sellersHistoryFlow.value = ResellApiResponse.Error
                Log.e("ChatRepository", "Error fetching buyer history: ", e)
            }
        }
    }

    /**
     * Starts loading the chat history and sends it down [buyersHistoryFlow].
     */
    fun fetchBuyersHistory() {
        _buyersHistoryFlow.value = ResellApiResponse.Pending
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val buyerData =
                    fireStoreRepository.getBuyerHistory(userInfoRepository.getEmail()!!)
                _buyersHistoryFlow.value = ResellApiResponse.Success(buyerData)
            } catch (e: Exception) {
                _buyersHistoryFlow.value = ResellApiResponse.Error
                Log.e("ChatRepository", "Error fetching buyer history: ", e)
            }
        }
    }
}
