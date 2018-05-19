package github.io.mssjsg.bookbag.util;

import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Sing on 7/8/2017.
 */

public class ItemUidGenerator {

    private RandomString mRandomString;

    @Inject
    public ItemUidGenerator() {
        mRandomString = new RandomString(3);
    }

    public String generateItemUid(String item) {
        String uuid = UUID.nameUUIDFromBytes(item.getBytes()).toString();
        StringBuilder idBuilder = new StringBuilder(uuid);
        idBuilder.append('|').append(System.currentTimeMillis())
                .append('|').append(mRandomString.nextString());
        return idBuilder.toString();
    }

    private static class RandomString {

        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ch++) {
                tmp.append(ch);
            }
            for (char ch = 'a'; ch <= 'z'; ch++) {
                tmp.append(ch);
            }
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1) {
                throw new IllegalArgumentException("length < 1: " + length);
            }
            buf = new char[length];
        }

        public String nextString() {
            for (int i = 0; i < buf.length; i++) {
                buf[i] = symbols[random.nextInt(symbols.length)];
            }
            return new String(buf);
        }
    }
}
