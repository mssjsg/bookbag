package github.io.mssjsg.bookbag.data.source

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface BookbagDataSource<Item> {
    fun saveItem(item: Item): Single<String>

    fun moveItem(id: String, folderId: String?): Single<Int>

    fun updateItem(item: Item): Single<String>

    fun deleteItems(ids: List<String>): Single<Int>

    fun getItem(id: String): Single<Item>

    fun getItems(folderId: String? = null) : Flowable<List<Item>>

    fun getDirtyItems() : Flowable<List<Item>>

    fun deleteAllItems(): Completable
}