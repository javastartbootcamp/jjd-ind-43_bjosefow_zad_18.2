package pl.javastart.couponcalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceCalculator {

    public double calculatePrice(List<Product> products, List<Coupon> coupons) {
        double sum = 0;
        if (coupons == null && products == null) {
            return 0.0;
        }
        if (coupons == null) {
            return getSumPriceWithoutDisc(products);
        } else {
            double sumWithDisc;
            double theBestSumWithDisc = 0;
            for (Coupon coupon : coupons) {
                if (!checkIfCouponHasCategory(coupon)) {
                    sumWithDisc = getSumPriceWithCouponForAll(products, coupon);
                } else {
                    sumWithDisc = getSumPriceWithCouponWithCat(products, coupon);
                }
                if (theBestSumWithDisc == 0 || theBestSumWithDisc > sumWithDisc) {
                    theBestSumWithDisc = sumWithDisc;
                }
            }
            sum = theBestSumWithDisc;
        }
        return sum;
    }

    private boolean checkIfCouponHasCategory(Coupon coupon) {
        Category category = coupon.getCategory();
        return category != null;
    }

    private double calculatePriceWithDiscount(double price, int discountInPercent) {
        double result = (1 - (double) discountInPercent / 100) * price;
        BigDecimal resultBig = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
        return resultBig.doubleValue();
    }

    private double getSumPriceWithCouponWithCat(List<Product> products, Coupon coupon) {
        double sum = 0;
        for (Product product : products) {
            if (product.getCategory().equals(coupon.getCategory())) {
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

    private double getSumPriceWithCouponForAll(List<Product> products, Coupon coupon) {
        double sum = 0;
        return products.stream()
                .map(product -> calculatePriceWithDiscount(product.getPrice(), coupon.getDiscountValueInPercents()))
                .reduce(sum, Double::sum);
    }
}