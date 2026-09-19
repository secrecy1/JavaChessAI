public class PRNG {
   private long x = 12345678901234567L; // stores value from lcg
   private long y = 98765432109876543L; // Xor shift generator
   private int z = 123456789; // MWC base state
   private int c = 362436069; // MWC carry over state 
   private int z2 = 362436069;
   private int c2 = 210886241 ;
    public long getRandomNumber()
    {  
        // (a * x + c) % m where m = 2^64 
         x = x * 6906969069L + 1234567L;
         y ^= y << 13;
         y ^= y >>> 17; // unsigned shift to make sure it stays positive cuz java dosent have unsigned longs
         y ^= y << 43;
        // using two 32 bit MWC's because java doesnt have 128 bit integers
        // low bits
         long t = 1791398085L * z + c;
         c = (int)(t >>> 32);
         z = (int) t;
         // high bits
         long t2 = 1924861023L * z2 + c2;
         c2 =(int)( t2 >>> 32);
         z2 = (int) t2;
         long m = (((long)z2) << 32) | (z & 0xFFFFFFFFL);

         return x + y + m;
         

    }
}
