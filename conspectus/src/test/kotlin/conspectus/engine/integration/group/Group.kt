package conspectus.engine.integration.group

interface Group {

    companion object {
        val PACKAGE = Group::class.java.`package`.name
    }
}