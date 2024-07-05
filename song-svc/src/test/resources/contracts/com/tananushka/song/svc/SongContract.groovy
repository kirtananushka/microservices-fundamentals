package contracts.com.tananushka.song.svc

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should save metadata and return SongIdResponse"
    request {
        method 'POST'
        url '/songs'
        body([
                id      : $(consumer(regex('[0-9]{1,10}')), producer(1)),
                artist  : $(consumer(anyNonBlankString()), producer('Test Artist')),
                name    : $(consumer(anyNonBlankString()), producer('Test Song')),
                album   : $(consumer(anyNonBlankString()), producer('Test Album')),
                year    : $(consumer(regex('[0-9]{4}')), producer('2022')),
                duration: $(consumer(regex('[0-9]{2}:[0-5][0-9]')), producer('03:45'))
        ])
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status 200
        body([
                resourceId: 1
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
