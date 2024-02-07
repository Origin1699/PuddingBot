package top.ikaori.bot.entity.dto;


import lombok.Data;

import java.util.List;

/**
 * @author origin
 */

@Data
public class EpicDTO {

    public Data data;
    public Extensions extensions;

    @lombok.Data
    public static class Data {
        public Catalog Catalog;
    }

    @lombok.Data
    public static class Catalog {
        public SearchStore searchStore;
    }

    @lombok.Data
    public static class SearchStore {
        public List<Element> elements;
        public Paging paging;
    }

    @lombok.Data
    public static class Element {
        public String title;
        public String id;
        public String namespace;
        public String description;
        public String effectiveDate;
        public String offerType;
        public String expiryDate;
        public String viewableDate;
        public String status;
        public boolean isCodeRedemptionOnly;
        public List<KeyImage> keyImages;
        public Seller seller;
        public String productSlug;
        public String urlSlug;
        public String url;
        public List<Item> items;
        public List<CustomAttribute> customAttributes;
        public List<Category> categories;
        public List<Tag> tags;
        public CatalogNs catalogNs;
        public List<OfferMapping> offerMappings;
        public Price price;
        public Promotions promotions;
    }

    @lombok.Data
    public static class KeyImage {
        public String type;
        public String url;
    }

    @lombok.Data
    public static class Seller {
        public String id;
        public String name;
    }

    @lombok.Data
    public static class Item {
        public String id;
        public String namespace;
    }

    @lombok.Data
    public static class CustomAttribute {
        public String key;
        public String value;
    }

    @lombok.Data
    public static class Category {
        public String path;
    }

    @lombok.Data
    public static class Tag {
        public String id;
    }

    @lombok.Data
    public static class CatalogNs {
        public List<Mapping> mappings;
    }

    @lombok.Data
    public static class Mapping {
        public String pageSlug;
        public String pageType;
    }

    @lombok.Data
    public static class OfferMapping {
        public String pageSlug;
        public String pageType;
    }

    @lombok.Data
    public static class Price {
        public TotalPrice totalPrice;
        public List<LineOffer> lineOffers;
    }

    @lombok.Data
    public static class TotalPrice {
        public int discountPrice;
        public int originalPrice;
        public int voucherDiscount;
        public int discount;
        public String currencyCode;
        public CurrencyInfo currencyInfo;
        public FmtPrice fmtPrice;
    }

    @lombok.Data
    public static class CurrencyInfo {
        public int decimals;
    }

    @lombok.Data
    public static class FmtPrice {
        public String originalPrice;
        public String discountPrice;
        public String intermediatePrice;
    }

    @lombok.Data
    public static class LineOffer {
        public List<AppliedRule> appliedRules;
    }

    @lombok.Data
    public static class AppliedRule {
        public String id;
        public String endDate;
        public DiscountSetting discountSetting;
    }

    @lombok.Data
    public static class DiscountSetting {
        public String discountType;
        public int discountPercentage;
    }

    @lombok.Data
    public static class Promotions {
        public List<PromotionalOfferContainer> promotionalOffers;
        public List<PromotionalOfferContainer> upcomingPromotionalOffers;
    }

    @lombok.Data
    public static class PromotionalOfferContainer {
        public List<PromotionalOffer> promotionalOffers;
    }

    @lombok.Data
    public static class PromotionalOffer {
        public String startDate;
        public String endDate;
        public DiscountSetting discountSetting;
    }

    @lombok.Data
    public static class Paging {
        public int count;
        public int total;
    }

    @lombok.Data
    public static class Extensions {
    }
}