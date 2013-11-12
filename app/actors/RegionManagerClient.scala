package actors

import akka.actor.Actor
import backend._
import akka.actor.Props
import backend.RegionManager.UpdateUserPosition
import akka.routing.FromConfig
import models.backend.UserPosition

object RegionManagerClient {
  def props(): Props = Props[RegionManagerClient]
}

/**
 * A client for the region manager, handles routing position updates to the right node.
 */
class RegionManagerClient extends Actor {

  val regionManagerRouter = context.actorOf(Props.empty.withRouter(FromConfig), "router")

  val settings = Settings(context.system)

  def receive = {
    case p: UserPosition =>
      // Calculate the regionId for the users position
      val regionId = settings.GeoFunctions.regionForPoint(p.position)
      // And send the update to the that region
      regionManagerRouter ! UpdateUserPosition(regionId, p)
  }
}