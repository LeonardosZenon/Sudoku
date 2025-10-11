package gr.leonzch.sudoku.propeties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProperies {

    @Value("${server.port}")
    private int port;



}
