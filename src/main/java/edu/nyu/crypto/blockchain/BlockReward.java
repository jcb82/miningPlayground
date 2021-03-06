package edu.nyu.crypto.blockchain;

@FunctionalInterface
public interface BlockReward {
    static final BlockReward ONE = (h, t) -> 1d;

    double computeBlockReward(int height, double timeToCreate);

    default void reset(){

    };
}
