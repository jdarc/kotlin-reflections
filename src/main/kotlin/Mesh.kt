class Mesh(val vertexBuffer: Array<Vertex>) {

    companion object {

        fun load(directives: String): Mesh {
            val vertices = mutableListOf<Vector3>()
            val normals = mutableListOf<Vector3>()
            val buffer = mutableListOf<Vertex>()
            directives.split('\n').filter { it.trim().isNotEmpty() }.forEach {
                val fragments = it.split(' ')
                when {
                    fragments[0].equals("v", true) -> {
                        val x = fragments[1].trim().toFloat()
                        val y = fragments[2].trim().toFloat()
                        val z = fragments[3].trim().toFloat()
                        vertices.add(Vector3(x, y, z))
                    }
                    fragments[0].equals("vn", true) -> {
                        val x = fragments[1].trim().toFloat()
                        val y = fragments[2].trim().toFloat()
                        val z = fragments[3].trim().toFloat()
                        normals.add(Vector3(x, y, z))
                    }
                    fragments[0].equals("f", true) -> {
                        val f0 = fragments[1].split("//").map { v -> v.trim().toInt() - 1 }
                        val f1 = fragments[2].split("//").map { v -> v.trim().toInt() - 1 }
                        val f2 = fragments[3].split("//").map { v -> v.trim().toInt() - 1 }
                        buffer.add(Vertex(vertices[f0[0]], normals[f0[1]]))
                        buffer.add(Vertex(vertices[f1[0]], normals[f1[1]]))
                        buffer.add(Vertex(vertices[f2[0]], normals[f2[1]]))
                    }
                }
            }
            return Mesh(buffer.toTypedArray())
        }
    }
}
