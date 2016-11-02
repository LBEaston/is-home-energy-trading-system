package agents.models;

/* https://www.originenergy.com.au/terms-and-conditions/qld-electricity-tariffs.html
	 * Offpeak is on weekends and between 10pm�7am on weekdays
	 * Shoulder time between 7am�4pm, 8pm�10pm
	 *
	 * Price per kwh averages around ~25-35cents in Australia, (17-20 off peak): https://www.ovoenergy.com/guides/energy-guides/average-electricity-prices-kwh.html
	 * http://cmeaustralia.com.au/wp-content/uploads/2013/09/FINAL-INTERNATIONAL-PRICE-COMPARISON-FOR-PUBLIC-RELEASE-29-MARCH-2012.pdf
	 */

public class RetailerDescriptor
{
    public boolean isOffPeak;

    public int peakTickCount;

    public double peakSellPrice;
    public double offPeakSellPrice;
    public double peakBuyPrice;
    public double offPeakBuyPrice;

    public int currentPeriodTickCount = 0;
}
