import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;
import java.util.Random;

public class trello {

    private String id, boardId, cardIdOne, cardIdTwo;
    private String[] list;
    Random r = new Random();

    public static RequestSpecification base() {
        RestAssured.baseURI = "https://api.trello.com/1/";
        RequestSpecification request = RestAssured.
                given().
                    header("Content-Type", "application/json").contentType(ContentType.JSON).accept(ContentType.JSON).
                    queryParam("key", "caf37e07632ddd0af2670f0233f69602").
                    queryParam("token", "ATTAeaded948c2ebcdc23f716a4051e507602d637815bd5a43a15fb0ec343868e94bD7F60CA2");
        return request;
    }

    @Test
    public void createBoard() {
        Response responseBoard = base().
                request().
                    basePath("boards/").
                    queryParam("name", "testinium").
                when().
                    post();
        this.id = responseBoard.jsonPath().get("id");
        System.out.println(id + " numaralı board oluşturuldu.");
    }

    @Test
    public void getIdList() {
        createBoard();
        Response responseIdList = base().
                request().
                    basePath("boards/" + id + "/lists").
                when().
                    get();
        this.boardId = responseIdList.jsonPath().get("id[0]");
    }

    @Test
    public void createCardNamedOne() {
        getIdList();
        Response response = base().
                request().
                    basePath("cards").
                    queryParam("name", "one").
                    queryParam("idList", boardId).
                when().
                    post();
        this.cardIdOne = response.jsonPath().get("id");
        System.out.println(cardIdOne + " numaralı ilk kart oluşturuldu.");
    }

    @Test
    public void createCardNamedTwo() {
        createCardNamedOne();
        Response response = base().
                request().
                    basePath("cards").
                    queryParam("name", "two").
                    queryParam("idList", boardId).
                when().
                    post();
        this.cardIdTwo = response.jsonPath().get("id");
        System.out.println(cardIdTwo +  " numaralı ikinci kart oluşturuldu.");
        list = new String[]{cardIdOne, cardIdTwo};
    }

    @Test
    public void updateRandomCard() {
        createCardNamedTwo();
        base().request().
                    basePath("cards" + list[r.nextInt(list.length)]).
                    queryParam("name", "random").
                when().
                    post();
    }

    @Test
    public void deleteCards() {
        updateRandomCard();
        for(int i = 0; i<list.length; i++) {
            base().
                    request().
                        basePath("cards" + list[i]).
                    when().
                        delete();
            System.out.println(list[i] + " numaralı kart silindi.");
        }
    }

    @Test
    public void deleteBoard() {
        deleteCards();
        base().
                request().
                    basePath("boards/" + id).
                when().
                    delete();
        System.out.println(id + " numaralı board silindi.");
    }
}
