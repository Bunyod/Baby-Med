package babymed

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.string.Uri

package object refinements {
  type Phone = String Refined MatchesRegex[W.`"""^[+][0-9]{12}$"""`.T]
  type Password = String Refined MatchesRegex[
    W.`"""^[a-zA-Z0-9]{6,32}$"""`.T
  ]
  type EmailAddress =
    String Refined MatchesRegex[W.`"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+[.][a-zA-Z]{2,}"`.T]
  type UriAddress = String Refined Uri
}