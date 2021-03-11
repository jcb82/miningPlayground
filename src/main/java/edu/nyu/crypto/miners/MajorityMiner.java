package edu.nyu.crypto.miners;

import edu.nyu.crypto.blockchain.Block;
import edu.nyu.crypto.blockchain.NetworkStatistics;

public class MajorityMiner extends CompliantMiner implements Miner {

    
    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
    }

	// TODO Override methods to implement Majority Mining
    
}
