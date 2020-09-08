package GAJI;

import GAJI.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired
    ProductRepository productRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLeft_Delete(@Payload Left left){
        if(left.isMe()){
            Iterator<Product> iterator = productRepository.findAll().iterator();
            while(iterator.hasNext()){
                Product product = iterator.next();
                if(product.getMemberId() == left.getId()) {
                    productRepository.deleteById(product.getId());
                }
            }
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCompleted_StatusChange(@Payload PaymentCompleted paymentCompleted){
        if(paymentCompleted.isMe()){
            System.out.println("Product " + paymentCompleted.getProductId().toString() + " 판매 완료" );
            Optional<Product> productOptional = productRepository.findById(paymentCompleted.getProductId());
            Product product = productOptional.get();
            product.setStatus("Sold out");
            productRepository.save(product);
        }


    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_StatusChange(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){
            System.out.println("Product " + paymentCanceled.getProductId().toString() + " 판매 취소" );
            Optional<Product> productOptional = productRepository.findById(paymentCanceled.getProductId());

            Product product = productOptional.get();
            product.setStatus("In stock");
            productRepository.save(product);
        }
    }

}
