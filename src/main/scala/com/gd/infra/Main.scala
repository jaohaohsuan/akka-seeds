package com.gd.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.actor.Address
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {

  val config: Config = ConfigFactory.load()

  val clusterName = config.getString("cluster.name")
  val portOfSeed = config.getInt("akka.remote.netty.tcp.port")


  val addresses = config.getString("cluster.seed-nodes").split(",").map { host => new Address("akka.tcp", clusterName, host.trim, portOfSeed) }

  implicit val system = ActorSystem(clusterName, config)

  val cluster = Cluster(system)
  cluster.joinSeedNodes(addresses.toList)

  sys.addShutdownHook {
    cluster.leave(cluster.selfAddress)
  }
}
