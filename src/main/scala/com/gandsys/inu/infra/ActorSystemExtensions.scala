package com.gandsys.inu.infra

import akka.actor.ActorSystem
import akka.cluster.Cluster

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ActorSystemExtensions {

  implicit class ActorSystemSysHook(system: ActorSystem) {

    val log = com.typesafe.scalalogging.Logger(this.getClass)

    def waitTerminated() = {
      Await.result(system.whenTerminated, Duration.Inf)
      log.info(s"${system.name} has shutdown gracefully")
    }
  }

}
