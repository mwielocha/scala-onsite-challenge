package com.example

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import com.example.service.BiddingService

/*
 * Add your logic here. Feel free to rearrange the code as you see fit,
 * this is just a starting point.
 */
class DspFrontend(service: BiddingService) extends Directives with BidResponseJsonFormats {
  def apply(): Route =
    extractExecutionContext { implicit ec =>
      path("bid_request") {
        get {
          parameters('auction_id, 'ip, 'bundle_name, 'connection_type) { (auction_id, ip, bundle_name, connection_type) =>
            complete {
              service.query(
                auction_id,
                bundle_name,
                "USD", ip,
                connection_type
              )
            }
          }
        }
      } ~ path("winner" / Segment) { auction_id =>
        get {
          complete {
            service.notify(auction_id)
              .map(_ => "OK")
          }
        }
      }
    }
}
