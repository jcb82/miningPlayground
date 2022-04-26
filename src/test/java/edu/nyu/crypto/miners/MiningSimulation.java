package edu.nyu.crypto.miners;

import com.google.common.collect.ImmutableList;

import edu.nyu.crypto.blockchain.*;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Originally created by Benedikt Bunz on 08.10.15.
 * Updates by Assimakis Kattis, Kevin Choi, Joseph Bonneau
 */
public class MiningSimulation {
    private final static Logger LOGGER = LoggerFactory.getLogger(MiningSimulation.class);


    // Initialize a list of compliant miners of the given mining powers
    private List<Miner> makeCompliantMiners(ImmutableList<Integer> powers) {
    	ArrayList<Miner> result = new ArrayList<Miner>();
    	powers.forEach(n -> result.add(new CompliantMiner("Miner " + (result.size() + 1), n, 1)));
    	return result;
	}

    @Test
    public void simulateCompliantMiners() {
        LOGGER.info("Simulating network of compliant miners");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(510, 150, 140, 10, 50, 50));

        runSimulation(miners, BlockReward.ONE, ChurnFunction.NO_CHURN);
    }

    @Test
    public void simulateDifferentNetworkPower() {
        LOGGER.info("Simulating network of compliant miners with varying connectivity");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(510, 150, 140, 100, 50, 50));

        //modify network connectivity
        miners.get(0).setConnectivity(10);
        miners.get(1).setConnectivity(5);
        miners.get(3).setConnectivity(3);

        ChurnFunction churn = new NormalChurnFunction(1, 1, new SimulationRandom(4567));
        runSimulation(miners, BlockReward.ONE, churn);
    }

    @Test
    public void simulate51PercentAttack1() {
        LOGGER.info("Simulating 55% attacker, low churn network");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(200, 100, 100, 40, 10));

        Miner attacker = new MajorityMiner("Attacker", 550, 1);
        miners.add(attacker);

        ChurnFunction churn = new NormalChurnFunction(1, 1, new SimulationRandom(1234));
        Map<String, Double> relativeProfits = runSimulation(miners, BlockReward.ONE, churn);
        Assertions.assertThat(relativeProfits.get(attacker.getId())).isGreaterThan(.98);
    }

	@Test
    public void simulate51PercentAttack2() {
        LOGGER.info("Simulating 51% attacker, high churn network");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(200, 100, 100, 50, 40));

        Miner attacker = new MajorityMiner("Attacker", 510, 1);
        miners.add(attacker);

        SimulationRandom rng = new SimulationRandom(2345);
        ChurnFunction churn = new NormalChurnFunction(5, 5, rng);
        Map<String, Double> relativeProfits = runSimulation(miners, BlockReward.ONE, churn);
        Assertions.assertThat(relativeProfits.get(attacker.getId())).isGreaterThan(.6);
    }


    @Test
    public void simulateSelfishMining1() {
        LOGGER.info("Simulating selfish miner at 40%, no churn");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(15, 15, 10, 10, 10));

        Miner attacker = new SelfishMiner("Attacker", 40, 1);
        miners.add(attacker);

        Map<String, Double> relativeProfits = runSimulation(miners, BlockReward.ONE, ChurnFunction.NO_CHURN);
        double attackerProfits = relativeProfits.get(attacker.getId());
        Assertions.assertThat(attackerProfits).isGreaterThan(.415);
    }

    @Test
    public void simulateSelfishMining2() {

        LOGGER.info("Simulating selfish miner at 31%, with churn");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(150, 150, 100, 100, 100));

    	Miner attacker = new SelfishMiner("Attacker", 270, 60);
        miners.add(attacker);

        ChurnFunction churn = new NormalChurnFunction(1, 1, new SimulationRandom(3456));
        Map<String, Double> relativeProfits = runSimulation(miners, BlockReward.ONE, churn);
        double attackerProfits = relativeProfits.get(attacker.getId());
        Assertions.assertThat(attackerProfits).isGreaterThan(.35);
    }

    @Test
    public void simulateFeeSniping1() {

        LOGGER.info("Simulating fee sniping miner at 30%, with churn");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(200, 150, 150, 100, 100));

        Miner attacker = new FeeSnipingMiner("Attacker", 300, 1);
        miners.add(attacker);

        BlockReward reward = new LognormalReward(new SimulationRandom(8765));
        Map<String, Double> relativeProfits = runSimulation(miners, reward, ChurnFunction.NO_CHURN);
        double attackerProfits = relativeProfits.get(attacker.getId());
        Assertions.assertThat(attackerProfits).isGreaterThan(.33);
    }

    @Test
    public void simulateFeeSniping2() {

        LOGGER.info("Simulating fee sniping miner at 29%, with churn");

        List<Miner> miners = makeCompliantMiners(ImmutableList.of(220, 190, 150, 130, 20));

        Miner attacker = new FeeSnipingMiner("Attacker", 290, 1);
        miners.add(attacker);

        SimulationRandom rng = new SimulationRandom(5678);
        BlockReward reward = new LognormalReward(rng);
        ChurnFunction churn = new NormalChurnFunction(0.5, 1, rng);
        Map<String, Double> relativeProfits = runSimulation(miners, reward, churn);
        double attackerProfits = relativeProfits.get(attacker.getId());
        Assertions.assertThat(attackerProfits).isGreaterThan(.31);
    }

    /**
     * Runs the simulation and returns a relative profit vector
     *
     * @param miners
     * @param rewardFunction
     * @param churnFunction
     * @return
     */

    private Map<String, Double> runSimulation(Collection<Miner> miners, BlockReward rewardFunction, ChurnFunction churnFunction) {
        int numIterations = 100;
        BitcoinNetwork networkController = new BitcoinNetwork(rewardFunction, churnFunction, 0.005, 0.02d);
        Map<String, Double> profits = new TreeMap<>();
        SimulationRandom rng = new SimulationRandom(2345);
        for (int i = 0; i < numIterations; ++i) {
            int numBlocks = (int) rng.sampleExponentialRandom(0.0001);
            rewardFunction.reset();
            miners.forEach(miner -> miner.resetHashRate());
            miners.forEach(miner -> miner.resetConnectivity());
            Block head = networkController.simulation(numBlocks, miners, rng);
            Assertions.assertThat(head.getHeight()).isLessThanOrEqualTo(numBlocks);
            Block current = head;
            while (current != null) {
                String winningMiner = current.getMinedBy();
                profits.merge(winningMiner, current.getBlockValue(), Double::sum);
                current = current.getPreviousBlock();
            }
        }
        Map<String, Double> relativeProfits = new TreeMap<>();
        double totalProfits = profits.values().stream().mapToDouble(Double::doubleValue).sum();
        for (String winningMiner : profits.keySet()) {
            double profit = profits.get(winningMiner);
            if (profit == 0.0)
            	continue;
            double relativeProfit = profit / totalProfits;
            LOGGER.info("{} made {}% of the profits", String.format("%8s", winningMiner), String.format("%02.2f", 100d * relativeProfit));
            relativeProfits.put(winningMiner, relativeProfit);
        }
        return relativeProfits;
    }
}
