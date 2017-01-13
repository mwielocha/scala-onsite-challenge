package com.example

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route

/*
 * Add your logic here. Feel free to rearrange the code as you see fit,
 * this is just a starting point.
 */
object DspFrontend extends Directives with BidResponseJsonFormats {
  def apply(): Route =
    path("bid_request") {
      get {
        parameters('auction_id, 'ip, 'bundle_name, 'connection_type) { (auction_id, ip, bundle_name, connection_type) =>
          complete {
            Bid(auction_id, 3.12, "USD", "http://videos-bucket.com/video123.mov", "something") : BidResponse
          }
        }
      }
    } ~ path("winner" / Segment) { auction_id =>
      get {
        complete {
          "OK"
        }
      }
    }
}
