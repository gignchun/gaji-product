package GAJI;

import GAJI.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired
    ProductRepository productRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_StatusChange(@Payload PaymentCompleted paymentCompleted){

        if(paymentCompleted.isMe()){
            Optional<Product> productOptional = productRepository.findById(paymentCompleted.getProductId());

            Product product = productOptional.get();
            product.setStatus("Sold out");
            productRepository.save(product);
        }


    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_StatusChange(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){
            Optional<Product> productOptional = productRepository.findById(paymentCanceled.getProductId());

            Product product = productOptional.get();
            product.setStatus("In stock");
            productRepository.save(product);
        }
    }

}
