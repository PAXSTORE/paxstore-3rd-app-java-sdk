package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CookieObject implements Serializable {

   @Expose
   @SerializedName("signature")
   private String cookieSignature;

   @Expose @SerializedName("expires")
   private String cookieExpires;

   @Expose @SerializedName("keyPairId")
   private String cookieKeyPairId;


   public String getCookieSignature() {
      return cookieSignature;
   }

   public void setCookieSignature(String cookieSignature) {
      this.cookieSignature = cookieSignature;
   }

   public String getCookieExpires() {
      return cookieExpires;
   }

   public void setCookieExpires(String cookieExpires) {
      this.cookieExpires = cookieExpires;
   }

   public String getCookieKeyPairId() {
      return cookieKeyPairId;
   }

   public void setCookieKeyPairId(String cookieKeyPairId) {
      this.cookieKeyPairId = cookieKeyPairId;
   }

   @Override
   public String toString() {
      return "CookieObject{" +
              "cookieSignature='" + cookieSignature == null? "null" : "not null" + '\'' +
              ", cookieExpires='" + cookieExpires == null? "null" : "not null" + '\'' +
              ", cookieKeyPairId='" + cookieKeyPairId == null? "null" : "not null" + '\'' +
              '}';
   }
}
