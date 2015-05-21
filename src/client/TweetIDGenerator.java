package client;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by tom on 21/05/15.
 */
public class TweetIDGenerator {
		private SecureRandom random = new SecureRandom();

		public String nextId() {
			return new BigInteger(130, random).toString(32);
		}
}
