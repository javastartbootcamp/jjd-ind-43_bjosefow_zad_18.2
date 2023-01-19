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
        }
        if (coupons.size() == 1) {
            Coupon coupon = coupons.get(0);
            if (!checkIfCouponHasCategory(coupon)) {
                return getSumPriceWithCouponForAll(products, coupon);
            } else {
                return getSumPriceWithCouponWithCat(products, coupon);
            }
        } else {
            double sumWithoutDisc = getSumPriceWithoutDisc(products);
            double theBestDiff = 0;
            for (Coupon coupon : coupons) {
                double sumWithDisc = getSumPriceWithCouponWithCat(products, coupon);
                double tempDiff = sumWithoutDisc - sumWithDisc;
                if (tempDiff > theBestDiff) {
                    theBestDiff = tempDiff;
                }
            }
            sum = sumWithoutDisc - theBestDiff;
        }
        return sum;
    }

    public boolean checkIfCouponHasCategory(Coupon coupon) {
        Category category = coupon.getCategory();
        return category != null;
    }

    public double calculatePriceWithDiscount(double price, int discountInPercent) {
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

    public double getSumPriceWithoutDisc(List<Product> products) {
        double sum = 0;
        return products.stream()
                .map(Product::getPrice)
                .reduce(sum, Double::sum);
    }

    public double getSumPriceWithCouponForAll(List<Product> products, Coupon coupon) {
        double sum = 0;
        return products.stream()
                .map(product -> calculatePriceWithDiscount(product.getPrice(), coupon.getDiscountValueInPercents()))
                .reduce(sum, Double::sum);
    }
}