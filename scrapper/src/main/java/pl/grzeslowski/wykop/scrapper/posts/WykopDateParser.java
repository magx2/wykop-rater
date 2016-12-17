package pl.grzeslowski.wykop.scrapper.posts;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
class WykopDateParser implements DateParser{
    private final SimpleDateFormat postDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    @Override
    public Date parse(String date) {
        try {
            return postDate.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse date " + date, e);
        }
    }
}
