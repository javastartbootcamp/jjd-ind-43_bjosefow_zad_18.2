package pl.javastart.couponcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceCalculator {

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        if (coupons == null && products == null) {
            return 0.0;
        }
        if (coupons == null) {
            return getSumPriceWithoutDisc(products);
        } else {
            double theBestSumWithDisc = Double.MAX_VALUE;
            for (Coupon coupon : coupons) {
                double sumWithDisc = getSumPriceWithCoupon(products, coupon);
                if (sumWithDisc < theBestSumWithDisc) {
                    theBestSumWithDisc = sumWithDisc;
                }
            }
            return theBestSumWithDisc;
        }
    }

    private double calculatePriceWithDiscount(double price, int discountInPercent) {
        double result = (1 - (double) discountInPercent / 100) * price;
        BigDecimal resultBig = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        return resultBig.doubleValue();
    }

    private double getSumPriceWithCoupon(List<Product> products, Coupon coupon) {
        double sum = 0;
        for (Product product : products) {
            if (coupon.getCategory() == null || product.getCategory().equals(coupon.getCategory())) {
                sum += calculatePriceWithDiscount(product.getPrice(), coupon.getDiscountValueInPercents());
            } else {
                sum += product.getPrice();
            }
        }
        return sum;
    }

    private double getSumPriceWithoutDisc(List<Product> products) {
        double sum = 0;
        return products.stream()
                .map(Product::getPrice)
                .reduce(sum, Double::sum);
    }
}