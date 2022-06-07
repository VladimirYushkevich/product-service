package com.yushkevich.mms.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
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
