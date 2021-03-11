package edu.nyu.crypto.miners;

public class SelfishMiner extends CompliantMiner implements Miner {

	public SelfishMiner(String id, int hashRate, int connectivity) {
		super(id, hashRate, connectivity);
	}
   
	// TODO Override methods to implement Selfish Mining
}
