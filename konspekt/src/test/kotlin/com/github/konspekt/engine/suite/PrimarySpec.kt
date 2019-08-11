package com.github.konspekt.engine.suite

import com.github.konspekt.Spec

class PrimarySpec : Spec({

    it("works in root") {
    }

    it("fails in root") {
        throw AssertionError()
    }

    describe("1st level") {

        it("works in 1st level describe") {
        }

        it("fails in 1st level describe") {
            throw AssertionError()
        }

        context("2nd level") {

            it("works in 2nd level context") {
            }
        }

        describe("2nd level") {

            it("works in 2nd level describe") {
            }
        }
    }

})