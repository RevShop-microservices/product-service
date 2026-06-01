package com.example.product_service;

import com.example.product_service.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoOperations;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"spring.cloud.discovery.enabled=false",
		"jwt.secret=mySuperSecretKeyForJwtSigningMustBeAtLeast256BitsLong!!"
})
class ProductServiceApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private ProductRepository productRepository;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private ReviewRepository reviewRepository;

	@MockBean
	private RecentlyViewedRepository recentlyViewedRepository;

	@MockBean
	private OrderItemRepository orderItemRepository;

	@MockBean
	private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

	@Test
	void contextLoads() {
	}

}
