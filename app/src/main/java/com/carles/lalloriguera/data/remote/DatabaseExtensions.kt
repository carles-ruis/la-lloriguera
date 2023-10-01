package com.carles.lalloriguera.data.remote

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val DEFAULT_TIMEOUT = 20_000L

fun <T> DatabaseReference.flowList(dataType: Class<T>): Flow<List<T>> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val listOfT: List<T> = mutableListOf<T?>().apply {
                for (item in snapshot.children) {
                    add(item.getValue(dataType))
                }
            }.filterNotNull()
            trySend(listOfT)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("TaskRemoteDatasource", "flowList:${error.message}")
            cancel(error.message)
        }
    }
    waitForConnection()
    addValueEventListener(listener)
    awaitClose { removeEventListener(listener) }
}

suspend fun <T> DatabaseReference.singleValueEvent(dataType: Class<T>): T = suspendCoroutine { continuation ->
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            continuation.resume(snapshot.getValue(dataType)!!)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("TaskRemoteDatasource", "singleValueEvent:${error.message}")
            continuation.resumeWithException(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)
}

suspend fun DatabaseReference.waitForConnection(timeout: Long = DEFAULT_TIMEOUT): Boolean = try {
    withTimeout(timeMillis = timeout) {
        suspendCancellableCoroutine { continuation ->

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isConnected = snapshot.getValue(Boolean::class.java)!!
                    if (isConnected) {
                        if (continuation.isCompleted) {
                            Log.i("TaskRemoteDatasource", "waitForConnection:onDataChanged:continuation already resumed")
                            return
                        }
                        continuation.resume(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (continuation.isCompleted) {
                        Log.i("TaskRemoteDatasource", "waitForConnection:onCancelled:continuation already resumed")
                        return
                    }
                    Log.w("TaskRemoteDatasource", "checkConnection:${error.message}")
                    continuation.resumeWithException(error.toException())
                }
            }
            database.getReference(".info/connected").addValueEventListener(listener)
        }
    }
} catch (e: TimeoutCancellationException) {
    throw NoConnectionCancellationException()
}

fun DatabaseReference.generateNodeId(): String {
    return System.currentTimeMillis().toString()
}

suspend fun <T> Task<T>.setValueListeners() = suspendCoroutine { continuation ->
    addOnSuccessListener { t: T -> continuation.resume(t) }
    addOnFailureListener { exception -> continuation.resumeWithException(exception) }
}

