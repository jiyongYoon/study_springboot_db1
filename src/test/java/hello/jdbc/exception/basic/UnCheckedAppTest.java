package hello.jdbc.exception.basic;

import java.sql.SQLException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnCheckedAppTest {

    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(controller::request)
            .isInstanceOf(RuntimeSQLException.class);
    }


    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                // 체크 예외가 던져짐
                runSQL();
                // 해당 체크 예외를 잡아서
            } catch (SQLException e) {
                // 언체크 예외(런타임 에외)로 변환하여 밖으로 던짐
                throw new RuntimeSQLException(e);
                // 이렇게 되면, 컨트롤러나 서비스 등 어차피 해당 문제를 처리하지 못하는 곳에서
                // 예외에 의존하지 않아도 되고, ControllerAdvice 등에서 공통적으로 처리가 가능하다.
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(String message) {
            super(message);
        }

        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

}
