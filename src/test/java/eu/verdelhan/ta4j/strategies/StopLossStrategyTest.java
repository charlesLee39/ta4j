/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Marc de Verdelhan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j.strategies;

import eu.verdelhan.ta4j.strategies.StopLossStrategy;
import eu.verdelhan.ta4j.strategies.JustBuyOnceStrategy;
import eu.verdelhan.ta4j.Operation;
import eu.verdelhan.ta4j.OperationType;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.mocks.MockIndicator;
import eu.verdelhan.ta4j.mocks.MockStrategy;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

public class StopLossStrategyTest {

    private MockIndicator<Double> indicator;

    @Before
    public void setUp() {
        indicator = new MockIndicator<Double>(new Double[] { 100d, 100d, 96d, 95d, 94d });
    }

    @Test
    public void testStopperShouldSell() {

        Strategy justBuy = new JustBuyOnceStrategy();
        Strategy stopper = new StopLossStrategy(indicator, justBuy, 5);

        Operation buy = new Operation(0, OperationType.BUY);
        Operation sell = new Operation(4, OperationType.SELL);

        Trade trade = new Trade();
        assertThat(stopper.shouldOperate(trade, 0)).isTrue();
        trade.operate(0);
        assertThat(trade.getEntry()).isEqualTo(buy);
        assertThat(stopper.shouldOperate(trade, 1)).isFalse();
        assertThat(stopper.shouldOperate(trade, 2)).isFalse();

        assertThat(stopper.shouldOperate(trade, 4)).isTrue();
        trade.operate(4);
        assertThat(trade.getExit()).isEqualTo(sell);
    }

    @Test
    public void testStopperShouldSellIfStrategySays() {

        Operation[] enter = new Operation[] { new Operation(0, OperationType.BUY), null, null, null, null };
        Operation[] exit = new Operation[] { null, new Operation(1, OperationType.SELL), null, null, null };

        Strategy sell1 = new MockStrategy(enter, exit);

        Strategy stopper = new StopLossStrategy(indicator, sell1, 500);

        Operation buy = new Operation(0, OperationType.BUY);
        Operation sell = new Operation(1, OperationType.SELL);

        Trade trade = new Trade();
        assertThat(stopper.shouldOperate(trade, 0)).isTrue();
        trade.operate(0);

        assertThat(trade.getEntry()).isEqualTo(buy);

        assertThat(stopper.shouldOperate(trade, 1)).isTrue();
        trade.operate(1);

        assertThat(trade.getExit()).isEqualTo(sell);
    }

}
