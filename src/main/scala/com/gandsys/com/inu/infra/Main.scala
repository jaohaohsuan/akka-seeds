package com.gandsys.com.inu.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {

  val config: Config = ConfigFactory.load()

  val clusterName = config.getString("cluster.name")

  implicit val system = ActorSystem(clusterName, config)
  implicit val ec = system.dispatcher

  val log = com.typesafe.scalalogging.Logger(this.getClass)

  val cluster = Cluster(system)

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
    log.info(s"$clusterName has shutdown gracefully")
  }
}
