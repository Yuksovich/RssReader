package yuriy.rssreader.utils;


import android.support.annotation.NonNull;
import yuriy.rssreader.database.SingleRSSEntry;

import java.io.Serializable;

public final class EntrySerializer {
    private EntrySerializer() {
        throw new UnsupportedOperationException();
    }

    public static SerializableEntry getSerializable(@NonNull final SingleRSSEntry entry) {
        return new SerializableEntry(entry);
    }

    public static final class SerializableEntry implements Serializable {
        private final String channelTitle;
        private final String channelDescription;
        private final String itemTitle;
        private final String itemDescription;
        private final String itemPubDate;
        private final boolean itemBeenViewed;

        private SerializableEntry(final SingleRSSEntry entry) {
            channelTitle = entry.getChannelTitle();
            channelDescription = entry.getChannelDescription();
            itemTitle = entry.getItemTitle();
            itemDescription = entry.getItemDescription();
            itemPubDate = entry.getItemPubDate();
            itemBeenViewed = entry.isBeenViewed();
        }

        public String getChannelTitle() {
            return channelTitle;
        }

        public String getChannelDescription() {
            return channelDescription;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public String getItemPubDate() {
            return itemPubDate;
        }

        public boolean isItemUnseen() {
            return !itemBeenViewed;
        }
    }
}
