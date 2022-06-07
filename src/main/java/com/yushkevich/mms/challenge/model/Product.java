package com.yushkevich.mms.challenge.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "name", columnDefinition = "text")
  private String name;

  @Column(name = "relation_path", columnDefinition = "text")
  private String relationPath;

  @Enumerated(EnumType.STRING)
  @Type(type = "product_status_type")
  @Column(name = "online_status", columnDefinition = "product_status_enum_type")
  private ProductStatus onlineStatus;

  @Column(name = "description_long", columnDefinition = "text")
  private String descriptionLong;

  @Column(name = "description_short", columnDefinition = "text")
  private String descriptionShort;
}
