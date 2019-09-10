package conspectus.samples

import org.mockito.Mockito

fun <T> verifyOnly(mock: T): T = Mockito.verify(mock, Mockito.only())