package com.gd.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.actor.Address
import akka.cluster.http.management.ClusterHttpManagement
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {

  val config: Config = ConfigFactory.load()

  val clusterName = config.getString("cluster.name")
  val portOfSeed = config.getInt("akka.remote.netty.tcp.port")

  implicit val system = ActorSystem(clusterName, config)
  implicit val ec = system.dispatcher

  val log = com.typesafe.scalalogging.Logger(this.getClass)

  val cluster = Cluster(system)

  val clusterMan = ClusterHttpManagement(cluster)
  clusterMan.start().onComplete { _ =>
    log.info("ClusterHttpManagement is up")
  }

  val addresses = config.getString("cluster.seed-nodes").split(",").map { host => new Address("akka.tcp", clusterName, host.trim, portOfSeed) }
  cluster.joinSeedNodes(addresses.toList)

  sys.addShutdownHook {
    cluster.leave(cluster.selfAddress)
    clusterMan.stop().onComplete { _ =>
      log.info("ClusterHttpManagement stopped.")
    }
  }
}
