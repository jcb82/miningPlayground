package edu.nyu.crypto.blockchain;

import java.util.Collection;

import edu.nyu.crypto.miners.Miner;


public interface ChurnFunction {
    static ChurnFunction NO_CHURN = (orphanRate, miners) -> {
        int totalHashRate = miners.stream().mapToInt(Miner::getHashRate).sum();
        int totalConnectivity = miners.stream().mapToInt(Miner::getConnectivity).sum();
        return new NetworkStatistics(orphanRate, totalHashRate, totalConnectivity);
    };

    NetworkStatistics churnNetwork(double orphanRate, Collection<Miner> miners);
}
