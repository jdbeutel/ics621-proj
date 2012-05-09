package btree

import com.sun.electric.database.geometry.btree.CachingPageStorageWrapper
import com.sun.electric.database.geometry.btree.PageStorage

/**
 * Created with IntelliJ IDEA.
 * User: jdb
 * Date: 5/8/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
class InstrumentedCachingPageStorageWrapper extends CachingPageStorageWrapper {

    def cacheReadPages
    def cacheWritePages

    public InstrumentedCachingPageStorageWrapper(PageStorage ps, int cacheSize, boolean asyncFlush) {
        super(ps, cacheSize, asyncFlush)
        clearStats()
    }

    void clearStats() {
        cacheReadPages = []
        cacheWritePages = []
    }

    @Override
    public CachedPage getPage(int pageid, boolean readBytes) {
        synchronized(this) {
            allCachedPages.clear()      // avoid using pages not currently in cache
        }
        if (cache.containsKey(pageid)) {
            cacheReadPages << pageid
        }
        def cp = super.getPage(pageid, readBytes)
        new InstrumentedCachedPageImpl(pageid, cp)
    }

    class InstrumentedCachedPageImpl extends CachedPageImpl {

        @Delegate CachedPageImpl delegate

        private InstrumentedCachedPageImpl(int pageid, delegate) {
            super(pageid, delegate.buf)
            this.delegate = delegate
        }

        @Override
        void setDirty() {
            cacheWritePages << delegate.pageId
            delegate.setDirty()
        }
    }
}
