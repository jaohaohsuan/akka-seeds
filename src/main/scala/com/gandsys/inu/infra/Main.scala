package com.gandsys.inu.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Main extends App {

  val log = com.typesafe.scalalogging.Logger(this.getClass)

  private val config: Config = ConfigFactory.load()

  log.debug(s"akka.remote.netty.tcp.port = ${config.getInt("akka.remote.netty.tcp.port")}")
  log.debug(s"akka.remote.netty.tcp.hostname = ${config.getString("akka.remote.netty.tcp.hostname")}")

  implicit val system = ActorSystem(config.getString("cluster.name"))
  implicit val ec = system.dispatcher

  val clusterCfg = new ClusterConfig(config.getConfig("cluster"))
  val cluster: Cluster = Cluster(system)
  cluster.joinSeedNodes(clusterCfg.addresses)

  val clusterMan = ClusterHttpManagement(cluster)
  clusterMan.start().onComplete { _ =>
    log.info("ClusterHttpManagement is up")
  }

  sys.addShutdownHook {
    cluster.leave(cluster.selfAddress)
    cluster.down(cluster.selfAddress)
    clusterMan.stop().onComplete { _ =>
      log.info("ClusterHttpManagement stopped.")
    }

    Await.result(system.whenTerminated, Duration.Inf)
    log.info(s"${system.name} has shutdown gracefully")
  }
}
