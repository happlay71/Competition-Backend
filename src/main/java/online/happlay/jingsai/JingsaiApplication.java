package online.happlay.jingsai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"online.happlay.jingsai"})
@MapperScan("online.happlay.jingsai.mapper")
@EnableTransactionManagement
@EnableScheduling
public class JingsaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JingsaiApplication.class, args);
        System.out.println("学生竞赛获奖管理系统后端启动成功");
    }

}
