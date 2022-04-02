package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;


public interface Miner {
    /**
     * @return the Block that the miner is currently extending. May not yet have been published.
     */
    Block currentlyMiningAt();

    /**
     * Use this to announce new blocks (or not)
     *
     * @return The block that the miner views as the current head of the chain. Could be an old block or a newly announced block or the head of a chain of newly announced blocks.
     */
    Block currentHead();

    /**
     * This method is used to inform the miner of new block.
     * It is possible to hear of the same block multiple times.
     * If the block was mined by this miner, the second parameter will be set to true.
     * @param block block A block that has recently been announced
     * @param isMinerMe boolean value indicating if the block was mined by this miner.
     */
    void blockMined(Block block, boolean isMinerMe);


    /**
     * A change in the network may have happened.
     * @param statistics The updated NetworkStatistics
     */
    void networkUpdate(NetworkStatistics statistics);

    /**
     * Initialize the miner with some genesis block and the initial network statistics.
     * The internal state of the miner must be completely reset by this function.
     * @param genesis The genesis block
     * @param statistics The original NetworkStatistics
     */
    void initialize(Block genesis, NetworkStatistics statistics);


    /**
     * Hash Rate
     *
     * @return the number of hashes per second
     */
    int getHashRate();

    /**
     *
     * @param hashRate sets the hash rate of the Miner
     */
    void setHashRate(int hashRate);

    /**
    *
    * Resets the hash rate of the Miner to its base value
    */
    void resetHashRate();


    /**
     * @return the inverse of the average ping time
     */
    int getConnectivity();

    /**
     * Sets the connectivity of the Miner
     * @param connectivity
     */
    void setConnectivity(int connectivity);

    /**
     * Resets the connectivity of the Miner to its original value
     */
    void resetConnectivity();

    /**
     * @return The miners Id
     */
    String getId();
}
