package babymed.services.messages.setup

import cats.MonadThrow
import cats.effect.Async
import cats.effect.Resource
import cats.effect.std.Console
import org.typelevel.log4cats.Logger
import skunk.Session

import babymed.services.messages.ServerEnvironment
import babymed.services.messages.boundary.Messages
import babymed.support.database.Migrations

case class ServiceEnvironment[F[_]: MonadThrow](
    config: Config,
    repositories: Repositories[F],
  ) {
  lazy val messages = new Messages[F](repositories.messages)
  lazy val toServer: ServerEnvironment[F] =
    ServerEnvironment(
      services = ServerEnvironment.Services(messages)
    )
}

object ServiceEnvironment {
  def make[F[_]: Async: Console: Logger]: Resource[F, ServiceEnvironment[F]] =
    for {
      config <- Resource.eval(ConfigLoader.load[F])
      _ <- Resource.eval(Migrations.run[F](config.migrations))

      resource <- ServiceResources.make[F](config)
      repositories = {
        implicit val session: Resource[F, Session[F]] = resource.postgres
        Repositories.make[F]
      }
    } yield ServiceEnvironment[F](config, repositories)
}
